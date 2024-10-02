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

- Exclude vanilla brewing recipes that have been added to the BC from being incidentally generated as SPRs
- Mechanism for forcing specific saved recipes to re-gen/remove
- Remove recipe if reuslt no longer exists

- Heat level for cauldrons and recipes
- Other intermediary crafting potions aside from awkward

- Salt crafting mechanism xx

- Door as multiblock reference
- Drops come from loot table targeting block state property (only bottom half drops item, top half does not drop anything)
- canSurvive() breaks if block below is not solid or if the other half is missing
- setPlacedBy() sets the other half of the door when placed
- playerWillDestroy (in bed, this breaks the other half if one is broken)

- Speed of clothesline as a function of height
- Dimensional recipes for clothesline!! (editors note: why?)
- PRE-CALCULATE CLOTHESLINE ITEM POSITIONS IN BLOCKENTITY, RETRIEVE IN RENDERER xx


- Frozen lake in ice cave (with snowflake!!) xx 
- Make ore flowers plantable on grass, but not spawnable
- Make ore flowers spawnable on stone, but not plantable
- Make ore flowers pottable 

- Extend simple block feature to make it not check canSurvive so we can place things like snow on ice
- Add lava canyons to volcanic caves - research how to do this. Maybe check nether biomes for custom carving/features? xx


1.20

- ARID CAVE, ICE CAVE xx
- LUNARBERRYBUSHPATCH, OREGEN !!! XX
- unstable blocks not working xx
- lava geyuser data gen not working xx
- recipe category xx
- blockitems not registering / showing up in creative menu (or jei?) xx
- potion effect icon xx
- CLOTHESLINES crafting bug xx
- Clean Biome classes and gen code after refac xx
- Herbalists Lectern showing potions xx
- Loot modifiers (wormroot) (low prio) xx
- looping scary cave sounds ! xx


NEOFORGE 1.21


- access transform names xx
- NeoForge networking changes a bit - see NF networking docs xx
- Adapt serializer fromNetwork to use builders so we don't have to declare the method in each recipe serializer class (low prio)
- Refactor use methods in blocks xx
- Aquifer freeze biome check error ! (low prio)
- Something weird with recipe book making many many recipes - see game log xx
- Generic potion icon - atlas ! xx
- Hopper on right side of clothesline xx
- All translations present xx
- Fix choppy animations (partial ticks? in client tickhandler)
- Known recipes don't sync to client on join server xx

- Recharge sanguine altar with amethysts crystals + change block model accordingly!
- Inheritable plant type that can be waterlogged (like usual) as well as can be filled with layers of snow like a snow layer. Could be cool to randomize the y of each instance so some plants look sunken in snow
- We should explore more ways to make plants more visually appealing / overhaul rendering / try new models so we can fill caves and biomes with them and its not boring
- Flowers on cactus bloom after rain
- Add a literal rain dance. Could be a DDR like minigame lol (use custom renderer for this - could be cool lol. Maybe attach custom renderer to an invis entity that follows player location)
- Volcanic cave has loot/ plant/ore that lets you multiply netherite production frm scrap. This way the volcanic cave has netherite rewards, but does not allow you to bypass collecting ancient debris in the nether xx
- Clothesline Recipe support JEI!!
- Item pedestal
- Silk touch potion effect (spawners) 
- Dungeons that spawn in volcanic cave / ice cave/ arid cave have a chance to replace the entity in the spawner with their bioems variants!! this would be rlly cool detail xx
- Add buried treasure to arid cave! xx
- Add berried treasure to arid cave !
- Achievement for berried treasure
- Achievements!
- Add tea brewing :)
- Implement neoforge config system
- Enchantable / quicker more durable brush
- Make decorative fire obtainable 
- Fix herbalists lectern sounds