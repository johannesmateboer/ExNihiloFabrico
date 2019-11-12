package exnihilofabrico.api.crafting

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import exnihilofabrico.util.getId
import net.fabricmc.fabric.api.tag.TagRegistry
import net.minecraft.entity.EntityType
import net.minecraft.tag.Tag
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry

class EntityTypeIngredient(tags: MutableCollection<Tag<EntityType<*>>> = mutableListOf(), matches: MutableSet<EntityType<*>> = mutableSetOf()):
    AbstractIngredient<EntityType<*>>(tags, matches) {

    constructor(vararg matches: EntityType<*>): this(mutableListOf(), matches.toMutableSet())
    constructor(vararg tags: Tag<EntityType<*>>): this(tags.toMutableList(), mutableSetOf())

    override fun serializeElement(t: EntityType<*>, context: JsonSerializationContext) =
        JsonPrimitive(t.getId().toString())

    companion object {
        val EMPTY = EntityTypeIngredient()

        fun fromJson(json: JsonElement, context: JsonDeserializationContext): EntityTypeIngredient =
            fromJson(json,
                context,
                { deserializeTag(it, context) },
                { deserializeMatch(it, context) },
                { tags: MutableCollection<Tag<EntityType<*>>>, matches: MutableSet<EntityType<*>> ->
                    EntityTypeIngredient(
                        tags,
                        matches
                    )
                })

        fun deserializeTag(json: JsonElement, context: JsonDeserializationContext) =
            TagRegistry.entityType(Identifier(json.asString.split("#").last()))
        fun deserializeMatch(json: JsonElement, context: JsonDeserializationContext) =
            Registry.ENTITY_TYPE[(Identifier(json.asString))]
    }
}