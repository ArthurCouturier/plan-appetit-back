package fr.planappetit.planappetitback.services

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import fr.planappetit.planappetitback.models.recipes.generation.RecipeFromOpenAI
import fr.planappetit.planappetitback.models.recipes.generation.RecipeGenerationParameters
import org.springframework.ai.chat.model.ChatResponse
import org.springframework.ai.chat.prompt.Prompt
import org.springframework.ai.converter.BeanOutputConverter
import org.springframework.ai.openai.OpenAiChatModel
import org.springframework.ai.openai.OpenAiChatOptions
import org.springframework.ai.openai.api.OpenAiApi.ChatModel.GPT_4_O_MINI
import org.springframework.ai.openai.api.ResponseFormat
import org.springframework.stereotype.Service

@Service
class RecipeGenerationService(
    private val openAiChatModel: OpenAiChatModel
) {

    val outputConverter: BeanOutputConverter<RecipeFromOpenAI> = BeanOutputConverter<RecipeFromOpenAI>(RecipeFromOpenAI::class.java)
    val jsonSchema: String? = outputConverter.jsonSchema
    val objectMapper = ObjectMapper()

    private fun createPrompt(params: RecipeGenerationParameters): String {
        return """
            Vous êtes un chef créatif et vous vivez à ${params.localisation}. La saison est ${
            params.seasons.stream().map { s -> "$s et " }
        }vous êtes particulièrement doué pour créer des recettes de saison pour votre emplacement.
            Créez une recette innovante ${if (params.ingredient != "") " basée autour des ingrédients suivants: " + params.ingredient else ""} ${if (params.allergens != "") " mais excluant pour causes d'allergies les ingrédients suivants: " + params.allergens else ""} pour votre restaurant, pour un budget de ${if (params.buyPrice.toDouble() >= 0) params.buyPrice else 10}€ par personne que nous vendrons pour ${if (params.sellingPrice.toDouble() >= 0) params.sellingPrice else "20"}€ par personne. ${if (params.vegan) "Cette recette doit absolument respecter le fait d'être vegan." else ""} Répondez à tout en français.
            ${if (params.book) "Les recettes suivantes seront une base d'inspiration pour votre création: " else ""}
            Veuillez ne pas inclure de contenu inapproprié ou offensant dans votre réponse. Veuillez également ne pas inclure de contenu protégé par des droits d'auteur ou vous semblant étrange, sensible ou offensant.
            Tous les champs seront en français, ormis les champs season, unit et category qui seront exclusivement en anglais et qui devront suivre les enums qui les traduissent. Prends bien garde à ne pas te tromper à ce sujet.
        """.trimIndent()
    }
    // Répond uniquement avec un JSON valide (pas de balises ```json, pas de texte supplémentaire) suivant précisément le schéma suivant pour les types: $jsonSchema

    private fun enforceRequiredProperties(schema: MutableMap<String, Any>) {
        // Si l'objet définit des propriétés, forçons "required" à contenir toutes les clés
        val properties = schema["properties"] as? Map<*, *>
        if (properties != null) {
            // On écrase la valeur existante (le cas échéant)
            schema["required"] = properties.keys.filterIsInstance<String>().toList()
            // Parcours récursif de chaque propriété
            for ((_, value) in properties) {
                if (value is MutableMap<*, *>) {
                    @Suppress("UNCHECKED_CAST")
                    enforceRequiredProperties(value as MutableMap<String, Any>)
                }
            }
        }
        // Si le schéma représente un tableau, on traite "items"
        val items = schema["items"]
        if (items is MutableMap<*, *>) {
            @Suppress("UNCHECKED_CAST")
            enforceRequiredProperties(items as MutableMap<String, Any>)
        }
        // Parcours des définitions dans "$defs" (le cas échéant)
        val defs = schema["defs"]
        if (defs is MutableMap<*, *>) {
            for ((_, value) in defs) {
                if (value is MutableMap<*, *>) {
                    @Suppress("UNCHECKED_CAST")
                    enforceRequiredProperties(value as MutableMap<String, Any>)
                }
            }
        }
    }

    fun registerRecipe(params: RecipeGenerationParameters): RecipeFromOpenAI {
        // 1. Convertir le schéma brut en une Map mutable
        val schemaMap: MutableMap<String, Any> = if (jsonSchema is String) {
            objectMapper.readValue(jsonSchema, object : TypeReference<MutableMap<String, Any>>() {})
        } else {
            objectMapper.convertValue(jsonSchema, object : TypeReference<MutableMap<String, Any>>() {})
        }
        // 2. Enforcer l'ajout du champ "required" dans chaque objet du schéma
        enforceRequiredProperties(schemaMap)

        val springJsonSchema = ResponseFormat.JsonSchema.builder()
            .schema(schemaMap)
            .strict(false)
            .build()

        // 3. Construction du ResponseFormat en passant le schéma mis à jour
        val responseFormat: ResponseFormat = ResponseFormat.builder()
            .type(ResponseFormat.Type.JSON_SCHEMA)
            .jsonSchema(springJsonSchema)
            .build()
        val prompt = Prompt(
            createPrompt(params),
            OpenAiChatOptions.builder()
                .responseFormat(responseFormat)
                .model(GPT_4_O_MINI)
                .build()
        )
        val response: ChatResponse = openAiChatModel.call(prompt)
        val content: String = response.result.output.content
        val recipe: RecipeFromOpenAI = outputConverter.convert(content)!!
        return recipe
    }
}
