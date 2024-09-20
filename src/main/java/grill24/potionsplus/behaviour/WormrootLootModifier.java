package grill24.potionsplus.behaviour;

import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import grill24.potionsplus.core.Items;
import grill24.potionsplus.core.LootModifiers;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Supplier;

public class WormrootLootModifier extends LootModifier {
    public static final Supplier<Codec<WormrootLootModifier>> CODEC = Suppliers.memoize(() -> RecordCodecBuilder.create(inst -> codecStart(inst)
            .and(ForgeRegistries.BLOCKS.getCodec().listOf().fieldOf("blocks").forGetter(m -> m.blocks))
            .apply(inst, WormrootLootModifier::new)
    ));

    private final List<Block> blocks;

    /**
     * Constructs a LootModifier.
     *
     * @param conditionsIn the ILootConditions that need to be matched before the loot is modified.
     */
    public WormrootLootModifier(LootItemCondition[] conditionsIn, List<Block> blocksIn) {
        super(conditionsIn);
        blocks = blocksIn;
    }

    @Override
    protected @NotNull ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        final Block blockBroken;
        if (context.hasParam(LootContextParams.BLOCK_STATE)) {
            Block block = context.getParamOrNull(LootContextParams.BLOCK_STATE).getBlock();
            for (Block b : blocks) {
                if (block == b) {
                    if (context.getRandom().nextInt(4) == 0) {
                        if (block == Blocks.ROOTED_DIRT) {
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

    @Override
    public Codec<? extends IGlobalLootModifier> codec() {
        return LootModifiers.WORMROOT.get();
    }
}
