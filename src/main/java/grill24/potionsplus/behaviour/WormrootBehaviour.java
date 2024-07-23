package grill24.potionsplus.behaviour;

import com.google.gson.JsonObject;
import grill24.potionsplus.core.Items;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.LootModifier;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class WormrootBehaviour extends LootModifier {
    private Block[] blocks;
    /**
     * Constructs a LootModifier.
     *
     * @param conditionsIn the ILootConditions that need to be matched before the loot is modified.
     */
    protected WormrootBehaviour(LootItemCondition[] conditionsIn, Block[] blocksIn) {
        super(conditionsIn);
        blocks = blocksIn;
    }

    @NotNull
    @Override
    protected List<ItemStack> doApply(List<ItemStack> generatedLoot, LootContext context) {
        final Block blockBroken;
        if(context.hasParam(LootContextParams.BLOCK_STATE)) {
            Block block = context.getParamOrNull(LootContextParams.BLOCK_STATE).getBlock();
            for (Block b : blocks) {
                if (block == b) {
                    if(context.getRandom().nextInt(4) == 0) {
                        if(block == Blocks.ROOTED_DIRT) {
                            generatedLoot.add(new ItemStack(net.minecraft.world.item.Items.DIRT, 1));
                        }

                        blockBroken = b;
                        generatedLoot.removeIf(stack -> Block.byItem(stack.getItem()) == blockBroken);
                        generatedLoot.add(new ItemStack(Items.WORMROOT.get(), 1));
                    }

                    break;
                }
            }
        }



        return generatedLoot;
    }

    public static class WormrootBehaviourSerializer extends GlobalLootModifierSerializer<WormrootBehaviour> {
        @Override
        public WormrootBehaviour read(ResourceLocation location, JsonObject object, LootItemCondition[] conditions) {
            // Read all properties "item0", "item1", "item2", etc. until no more items are found
            List<Block> blocks = new ArrayList<>();
            int i = 0;
            while (object.has("block" + i)) {
                blocks.add(Block.byItem(GsonHelper.getAsItem(object, "block" + i)));
                i++;
            }

            return new WormrootBehaviour(conditions, blocks.toArray(new Block[0]));
        }

        @Override
        public JsonObject write(WormrootBehaviour instance) {
            JsonObject res = this.makeConditions(instance.conditions);
            for (int i = 0; i < instance.blocks.length; i++) {
                res.addProperty("block" + i, instance.blocks[i].asItem().getRegistryName().toString());
            }

            return null;
        }
    }
}
