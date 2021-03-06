package exnihilofabrico.modules.tools

import exnihilofabrico.api.registry.ExNihiloRegistries
import exnihilofabrico.modules.ModTags.CROOK_TAG
import net.minecraft.block.BlockState
import net.minecraft.item.ItemStack
import net.minecraft.item.ToolMaterial

class CrookTool(material: ToolMaterial, settings: Settings):
    ToolItemWithRegistry(material, ExNihiloRegistries.CROOK, settings) {

    override fun isEffectiveOn(state: BlockState) = ExNihiloRegistries.CROOK.isRegistered(state.block)

    companion object {
        @JvmStatic
        fun isCrook(stack: ItemStack): Boolean {
            return (stack.item is CrookTool || stack.item.isIn(CROOK_TAG))
        }
    }

}