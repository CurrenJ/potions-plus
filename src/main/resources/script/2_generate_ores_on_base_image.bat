set top_alpha=%2
set bottom_alpha=%3
set output=%4

py .\paste_image_atop.py %1 input_ore/copper_ore_isolated.png output/copper_ore_a_%1 default 1
py .\paste_image_atop.py output/copper_ore_a_%1 ore_edges/copper_ore_top.png output/copper_ore_b_%1 default %top_alpha%
py .\paste_image_atop.py output/copper_ore_b_%1 ore_edges/copper_ore_bottom.png output/%output%_copper_ore.png default %bottom_alpha%
del output\copper_ore_a_%1 output\copper_ore_b_%1

py .\paste_image_atop.py %1 input_ore/diamond_ore_isolated.png output/diamond_ore_a_%1 default 1
py .\paste_image_atop.py output/diamond_ore_a_%1 ore_edges/diamond_ore_top.png output/diamond_ore_b_%1 default %top_alpha%
py .\paste_image_atop.py output/diamond_ore_b_%1 ore_edges/diamond_ore_bottom.png output/%output%_diamond_ore.png default %bottom_alpha%
del output\diamond_ore_a_%1 output\diamond_ore_b_%1

py .\paste_image_atop.py %1 input_ore/emerald_ore_isolated.png output/emerald_ore_a_%1 default 1
py .\paste_image_atop.py output/emerald_ore_a_%1 ore_edges/emerald_ore_top.png output/emerald_ore_b_%1 default %top_alpha%
py .\paste_image_atop.py output/emerald_ore_b_%1 ore_edges/emerald_ore_bottom.png output/%output%_emerald_ore.png default %bottom_alpha%
del output\emerald_ore_a_%1 output\emerald_ore_b_%1

py .\paste_image_atop.py %1 input_ore/gold_ore_isolated.png output/gold_ore_a_%1 default 1
py .\paste_image_atop.py output/gold_ore_a_%1 ore_edges/gold_ore_top.png output/gold_ore_b_%1 default %top_alpha%
py .\paste_image_atop.py output/gold_ore_b_%1 ore_edges/gold_ore_bottom.png output/%output%_gold_ore.png default %bottom_alpha%
del output\gold_ore_a_%1 output\gold_ore_b_%1

py .\paste_image_atop.py %1 input_ore/lapis_ore_isolated.png output/lapis_ore_a_%1 default 1
py .\paste_image_atop.py output/lapis_ore_a_%1 ore_edges/lapis_ore_top.png output/lapis_ore_b_%1 default %top_alpha%
py .\paste_image_atop.py output/lapis_ore_b_%1 ore_edges/lapis_ore_bottom.png output/%output%_lapis_ore.png default %bottom_alpha%
del output\lapis_ore_a_%1 output\lapis_ore_b_%1

py .\paste_image_atop.py %1 input_ore/redstone_ore_isolated.png output/redstone_ore_a_%1 default 1
py .\paste_image_atop.py output/redstone_ore_a_%1 ore_edges/redstone_ore_top.png output/redstone_ore_b_%1 default %top_alpha%
py .\paste_image_atop.py output/redstone_ore_b_%1 ore_edges/redstone_ore_bottom.png output/%output%_redstone_ore.png default %bottom_alpha%
del output\redstone_ore_a_%1 output\redstone_ore_b_%1

py .\paste_image_atop.py %1 input_ore/iron_ore_isolated.png output/iron_ore_a_%1 default 1
py .\paste_image_atop.py output/iron_ore_a_%1 ore_edges/iron_ore_top.png output/iron_ore_b_%1 default %top_alpha%
py .\paste_image_atop.py output/iron_ore_b_%1 ore_edges/iron_ore_bottom.png output/%output%_iron_ore.png default %bottom_alpha%
del output\iron_ore_a_%1 output\iron_ore_b_%1

py .\paste_image_atop.py %1 input_ore/coal_ore_isolated.png output/coal_ore_a_%1 default 1
py .\paste_image_atop.py output/coal_ore_a_%1 ore_edges/coal_ore_top.png output/coal_ore_b_%1 default %top_alpha%
py .\paste_image_atop.py output/coal_ore_b_%1 ore_edges/coal_ore_bottom.png output/%output%_coal_ore.png default %bottom_alpha%
del output\coal_ore_a_%1 output\coal_ore_b_%1