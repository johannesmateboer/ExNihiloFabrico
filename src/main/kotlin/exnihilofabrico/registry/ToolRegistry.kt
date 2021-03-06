package exnihilofabrico.registry

import com.google.gson.reflect.TypeToken
import exnihilofabrico.api.recipes.ToolRecipe
import exnihilofabrico.api.registry.IToolRegistry
import exnihilofabrico.compatibility.rei.tools.ToolCategory
import exnihilofabrico.util.ofSize
import net.minecraft.item.ItemConvertible
import net.minecraft.item.ItemStack
import java.io.File
import java.io.FileReader
import java.lang.reflect.Type
import java.util.*

data class ToolRegistry(val registry: MutableList<ToolRecipe> = mutableListOf()):
    AbstractRegistry<MutableList<ToolRecipe>>(), IToolRegistry {
    override fun clear() = registry.clear()

    override fun register(recipe: ToolRecipe): Boolean {
        val match = registry.firstOrNull { recipe.ingredient == it.ingredient }
        if(match == null)
            return registry.add(recipe)
        else
            match.lootables.addAll(recipe.lootables)
        // TODO use ToolManager to set break by tools
        return false
    }

    override fun isRegistered(target: ItemConvertible) = registry.any { it.ingredient.test(target.asItem()) }

    override fun getResult(target: ItemConvertible, rand: Random): MutableList<ItemStack> {
        return getAllResults(target)
            .map { loot ->
                val amount = loot.chance.count { chance -> chance > rand.nextDouble() }
                if(amount > 0) loot.stack.ofSize(amount) else ItemStack.EMPTY
            }
            .filter { !it.isEmpty }
            .toMutableList()
    }
    override fun getAllResults(target: ItemConvertible) =
        registry.filter { it.ingredient.test(target.asItem()) }.map { it.lootables }.flatten()

    override fun registerJson(file: File) {
        val json: MutableList<ToolRecipe> = gson.fromJson(FileReader(file), SERIALIZATION_TYPE)
        json.forEach { register(it.ingredient, it.lootables) }
    }
    override fun serializable() = registry
    override fun getREIRecipes() =
        registry.map { recipe ->
            recipe.lootables.chunked(ToolCategory.MAX_OUTPUTS) {
                ToolRecipe(recipe.ingredient, it.toMutableList())
            }
        }.flatten()

    companion object {
        val SERIALIZATION_TYPE: Type = object: TypeToken<MutableList<ToolRecipe>>(){}.type
        fun fromJson(file: File, defaults: (IToolRegistry) -> Unit) = fromJson(file, {ToolRegistry()}, defaults)
    }

}