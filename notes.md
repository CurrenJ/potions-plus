# References

## Potions Master

- [TileEntityCauldron](https://github.com/thevortex/PotionsMaster/blob/master/src/main/java/com/thevortex/potionsmaster/entity/TileEntityCauldron.java)

## Botania

- [RunicAltarBlocKEntityRenderer](https://github.com/VazkiiMods/Botania/blob/780e6fafb9543aef2f78c19ea1eab2e26c36977a/Xplat/src/main/java/vazkii/botania/client/render/block_entity/RunicAltarBlockEntityRenderer.java#L11)
- [SimpleInventoryBlockEntity](https://github.com/VazkiiMods/Botania/blob/1.20.x/Xplat/src/main/java/vazkii/botania/common/block/block_entity/SimpleInventoryBlockEntity.java#L24)
- [RunicAltarBlock](https://github.com/VazkiiMods/Botania/blob/ddf9dea06827671fde7e8f6922cd5573fe749695/Xplat/src/main/java/vazkii/botania/common/block/mana/RunicAltarBlock.java#L41)

## Forge
- [Forge Docs](https://docs.minecraftforge.net/en/1.18.x/gettingstarted/)

## Minecraft Code References

### Cauldron Stuff
- `water_cauldron.json` BlockState references `water_cauldron_levelX` models
- `water_cauldron_levelX.json` model sets textures and has parent `template_cauldron_level1`
- `template_cauldron_level1.json` is a BlockModel and is used to create a ModelTemplate in `ModelTemplates.java` 
  - Texture slots used in models are specified here
  - Specific texture slot constants defined in `TextureSlot.java`
- `createCauldrons()` in `BlockModelGenerators.java` uses ModelTemplate to generate BlockStates for each cauldron level

### Water Cauldron Data Flow
- `water_cauldron.json` BlockState -> `water_cauldron_levelX.json` Model -> `template_cauldron_levelX.json`
- 

#### `water_cauldron.json` BlockState generation in `BlockModelGenerators.java`:
- `this.blockStateOutput.accept(MultiVariantGenerator.multiVariant(Blocks.WATER_CAULDRON)`  
- `.with(PropertyDispatch.property(LayeredCauldronBlock.LEVEL)`  
  - `.select(1, Variant.variant().with(VariantProperties.MODEL, ModelTemplates.CAULDRON_LEVEL1.createWithSuffix(Blocks.WATER_CAULDRON, "_level1", TextureMapping.cauldron(TextureMapping.getBlockTexture(Blocks.WATER, "_still")), this.modelOutput)))`  
  - `.select(2, Variant.variant().with(VariantProperties.MODEL, ModelTemplates.CAULDRON_LEVEL2.createWithSuffix(Blocks.WATER_CAULDRON, "_level2", TextureMapping.cauldron(TextureMapping.getBlockTexture(Blocks.WATER, "_still")), this.modelOutput)))`  
  - `.select(3, Variant.variant().with(VariantProperties.MODEL, ModelTemplates.CAULDRON_FULL.createWithSuffix(Blocks.WATER_CAULDRON, "_full", TextureMapping.cauldron(TextureMapping.getBlockTexture(Blocks.WATER, "_still")), this.modelOutput)))`  
- `));`

### Particles

- `ParticleEngine.java` registers particles and links `ParticleType` to Particle's class.
- `ParticleTypes.java` defines ParticleType instances'
- `HugeExplosionSeedParticle.java` is a no-render particle that acts as an emitter with a lifetime



- Clock item model can help us with the "effect" icon

# To Do

9- BUG: Failed recipe ingredient sampling adds empty recipe like so:
`- [BCR] levitation_a3_d3_splash_potion=> levitation_a4_d3_splash_potion
  [BCR] levitation_a3_d3_lingering_potion=> levitation_a4_d3_lingering_potion
  [BCR] totem_of_undying + nautilus_shell + levitation_a4_d2_potion=> levitation_a4_d3_potion
  [BCR] totem_of_undying + nautilus_shell + levitation_a4_d2_splash_potion=> levitation_a4_d3_splash_potion
  [BCR] totem_of_undying + nautilus_shell + levitation_a4_d2_lingering_potion=> levitation_a4_d3_lingering_potion
  [BCR] levitation_a4_d0_potion=> levitation_a5_d0_potion
  [BCR] levitation_a4_d0_splash_potion=> levitation_a5_d0_splash_potion
  [BCR] levitation_a4_d0_lingering_potion=> levitation_a5_d0_lingering_potion
  [BCR] levitation_a4_d1_potion=> levitation_a5_d1_potion
  [BCR] levitation_a4_d1_splash_potion=> levitation_a5_d1_splash_potion
  [BCR] levitation_a4_d1_lingering_potion=> levitation_a5_d1_lingering_pot`
- - Make it not add that recipe

- Fix jei integration for cauldron recipes
- design and add knowledge system
- Visual?? Block entity with custom renderer again?


- Uniqueness not working????
  `[BCR] stick + awkward_potion=> teleportation_a0_d0_potion
  [BCR] stick + awkward_splash_potion=> teleportation_a0_d0_splash_potion
  [BCR] stick + awkward_lingering_potion=> teleportation_a0_d0_lingering_potion
  [BCR] redstone + pufferfish + teleportation_a0_d0_potion=> teleportation_a1_d0_potion
  [BCR] redstone + pufferfish + teleportation_a0_d0_splash_potion=> teleportation_a1_d0_splash_potion
  [BCR] redstone + pufferfish + teleportation_a0_d0_lingering_potion=> teleportation_a1_d0_lingering_potion
  [BCR] amethyst_cluster + magma_cream + teleportation_a1_d0_potion=> teleportation_a2_d0_potion
  [BCR] amethyst_cluster + magma_cream + teleportation_a1_d0_splash_potion=> teleportation_a2_d0_splash_potion
  [BCR] amethyst_cluster + magma_cream + teleportation_a1_d0_lingering_potion=> teleportation_a2_d0_lingering_potion
  [BCR] nautilus_shell + heart_of_the_sea + wither_rose + teleportation_a2_d0_potion=> teleportation_a3_d0_potion
  [BCR] nautilus_shell + heart_of_the_sea + wither_rose + teleportation_a2_d0_splash_potion=> teleportation_a3_d0_splash_potion
  [BCR] nautilus_shell + heart_of_the_sea + wither_rose + teleportation_a2_d0_lingering_potion=> teleportation_a3_d0_lingering_potion
  [BCR] stick + awkward_potion=> magnetic_a0_d0_potion
  [BCR] stick + awkward_splash_potion=> magnetic_a0_d0_splash_potion`