# Mise en place d'un Environnement de Production pour l'API via Kubernetes, Ingress Nginx et cert-manager

Ce guide décrit comment déployer un environnement de production pour votre API backend (par exemple, `plan-appetit-back`) dans un cluster Kubernetes. Nous utilisons :

- **Vercel** pour gérer le DNS de votre domaine (acheté sur Hostinger)
- **cert-manager** pour obtenir un certificat TLS valide via Let's Encrypt
- **Nginx Ingress Controller** pour gérer le routage du trafic HTTPS vers votre API
- Une **ressource Ingress** pour rediriger le trafic vers le service backend

## Table des matières

- [Prérequis](#prérequis)
- [Étape 1 : Configuration DNS](#étape-1--configuration-dns)
- [Étape 2 : Installation de cert-manager et obtention d'un certificat TLS](#étape-2--installation-de-cert-manager-et-obtention-dun-certificat-tls)
    - [Créer un ClusterIssuer](#créer-un-clusterissuer)
    - [Créer une ressource Certificate](#créer-une-ressource-certificate)
- [Étape 3 : Déploiement de l'Ingress Controller](#étape-3--déploiement-de-lingress-controller)
    - [Option via manifest YAML (officiel)](#option-via-manifest-yaml-officiel)
    - [Vérification et configuration du service](#vérification-et-configuration-du-service)
- [Étape 4 : Création de la ressource Ingress pour l'API](#étape-4--création-de-la-ressource-ingress-pour-lapi)
- [Étape 5 : Tests et Vérification](#étape-5--tests-et-vérification)
- [Fonctionnement global](#fonctionnement-global)
- [Remarques et bonnes pratiques](#remarques-et-bonnes-pratiques)

---

## Prérequis

- Un cluster Kubernetes opérationnel (par exemple, sur un VPS)
- Le domaine principal (ex. `plan-appetit.fr`) géré par Vercel
- Un sous-domaine dédié pour l'API, par exemple `api.plan-appetit.fr`
- Accès en ligne de commande (kubectl) et configuration de Helm (si nécessaire)

---

## Étape 1 : Configuration DNS

1. **Ajouter un enregistrement DNS sur Vercel :**
    - Connectez-vous à votre tableau de bord Vercel.
    - Dans la section **Domains**, ajoutez un enregistrement de type **A** pour le sous-domaine `api` :
        - **Nom/Host :** `api`
        - **Valeur/IP :** l'IP publique de votre VPS (ex. `93.127.162.136`).

2. **Vérifier la propagation DNS :**
   ```bash
   dig api.plan-appetit.fr +short
    ```

La réponse doit être l'IP de votre VPS.

---

## Étape 2 : Installation de cert-manager et obtention d'un certificat TLS

### Installer cert-manager
Ajouter le dépôt Helm de cert-manager et installer avec Helm :

```bash
helm repo add jetstack https://charts.jetstack.io
helm repo update
kubectl create namespace cert-manager
helm install cert-manager jetstack/cert-manager --namespace cert-manager --version v1.9.1 --set installCRDs=true
```

### Créer un ClusterIssuer
Créer un fichier clusterissuer.yaml :

```
apiVersion: cert-manager.io/v1
kind: ClusterIssuer
metadata:
  name: letsencrypt-prod
spec:
  acme:
    server: https://acme-v02.api.letsencrypt.org/directory
    email: votre.email@example.com  # Remplacez par votre email
    privateKeySecretRef:
      name: letsencrypt-prod
    solvers:
    - http01:
        ingress:
          class: nginx
```

Créer une ressource Certificate
Créer un fichier certificate.yaml dans le namespace où se trouve votre Ingress (ex. test-db) :

```bash
apiVersion: cert-manager.io/v1
kind: Certificate
metadata:
  name: tls-secret-for-api
  namespace: test-db
spec:
  secretName: tls-secret-for-api
  issuerRef:
    name: letsencrypt-prod
    kind: ClusterIssuer
  commonName: api.plan-appetit.fr
  dnsNames:
    - api.plan-appetit.fr
```

Appliquer le certificat :

```
kubectl apply -f certificate.yaml -n test-db
```

---

### Étape 3 : Déploiement de l'Ingress Controller

Option via manifest YAML (officiel)
Appliquer le manifest officiel :

```
kubectl apply -f https://raw.githubusercontent.com/kubernetes/ingress-nginx/main/deploy/static/provider/cloud/deploy.yaml
```

Cela déploie l'Ingress Controller dans le namespace ingress-nginx.
Vérification et configuration du service

Vérifiez les pods :

```
kubectl get pods -n ingress-nginx
```

Modifier le service pour exposer HTTPS sur le port 8443 :
Éditez le service ingress-nginx-controller :

```
kubectl edit svc ingress-nginx-controller -n ingress-nginx
```

Dans la section ports, configurez-le ainsi :

```bash
ports:
- name: http
  port: 80
  targetPort: 80
  protocol: TCP
- name: https
  port: 8443
  targetPort: 443
  protocol: TCP

```

Sauvegardez et vérifiez :

```
kubectl get svc -n ingress-nginx ingress-nginx-controller
```

---

### Étape 4 : Création de la ressource Ingress pour l'API

Créez un fichier ingress.yaml dans le namespace test-db :

```bash
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: plan-appetit-back-ingress
  namespace: test-db
  annotations:
    nginx.ingress.kubernetes.io/disable-validation: "true"
    nginx.ingress.kubernetes.io/enable-cors: "true"
    nginx.ingress.kubernetes.io/cors-allow-origin: "https://develop.plan-appetit.fr"
    nginx.ingress.kubernetes.io/cors-allow-methods: "GET, POST, PUT, DELETE, PATCH, OPTIONS"
    nginx.ingress.kubernetes.io/cors-allow-headers: "DNT,User-Agent,X-Requested-With,If-Modified-Since,Cache-Control,Content-Type,Range,Authorization,Email"
    nginx.ingress.kubernetes.io/cors-expose-headers: "Content-Length,Content-Range"
spec:
  ingressClassName: nginx
  tls:
    - hosts:
        - api.plan-appetit.fr
      secretName: tls-secret-for-api
  rules:
    - host: api.plan-appetit.fr
      http:
        paths:
          - path: /
            pathType: Prefix
            backend:
              service:
                name: plan-appetit-back
                port:
                  number: 8080
```

Appliquez la ressource :

kubectl apply -f ingress.yaml -n test-db

---

### Étape 5 : Tests et Vérification

Vérifier le DNS :
dig api.plan-appetit.fr +short
La réponse doit renvoyer l'IP externe de votre Ingress Controller.
Tester avec curl :
curl -v https://api.plan-appetit.fr:8443/
Vous devriez voir le certificat délivré par Let's Encrypt (et non le "Fake Certificate").
La requête doit être acheminée vers votre API backend.
Tester depuis le front-end :
Assurez-vous que le front déployé sur Vercel pointe vers :
const response = await fetch("https://api.plan-appetit.fr/api/v1/recipes/all", { /* options */ });
Le navigateur ne doit plus afficher d'erreur de certificat ni d'erreur CORS.

---

### Fonctionnement Global

## DNS :
Le domaine api.plan-appetit.fr pointe vers l'IP publique de votre VPS (via Vercel).

## Ingress Controller :
Reçoit le trafic HTTPS sur le port 8443.
Terminaison TLS effectuée grâce au certificat délivré par Let's Encrypt (via cert-manager et le secret tls-secret-for-api).
Redirige le trafic en HTTP vers le service backend.

## Ressource Ingress :
Définie dans le namespace test-db, elle indique que tout le trafic destiné à api.plan-appetit.fr doit être routé vers le service plan-appetit-back sur le port 8080.
L'annotation ingressClassName: nginx assure que cette ressource est gérée par le contrôleur Ingress nginx.

## Backend API :
Le service plan-appetit-back (de type ClusterIP) reçoit le trafic en HTTP sur le port 8080 et le transmet à vos pods d'application.

---

### Remarques et Bonnes Pratiques

## Sécurité TLS :
Utilisez cert-manager pour automatiser le renouvellement des certificats. Vérifiez régulièrement l'état des ressources Certificate et ClusterIssuer.

## IngressClass :
Assurez-vous que la ressource Ingress spécifie ingressClassName: nginx pour être prise en compte par le contrôleur.

## Monitoring et Logs :
Surveillez les logs du contrôleur Ingress (kubectl logs -n ingress-nginx ...) et du backend pour détecter d'éventuelles erreurs de routage ou de sécurité.

## Environnements multiples :
Pour déployer dans d'autres environnements (staging, production), vous pouvez réutiliser cette configuration en adaptant :
Les noms de domaines et enregistrements DNS.

Les paramètres du ClusterIssuer (par exemple, utiliser letsencrypt-staging pour des tests).
En suivant ces étapes, vous aurez un environnement de production complet et reproductible pour votre API. N'hésitez pas à adapter ce guide en fonction de vos besoins spécifiques et à consulter la documentation de cert-manager et ingress-nginx pour plus de détails.
