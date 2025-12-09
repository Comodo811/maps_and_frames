package io.github.comodo811.mapsandframes.display;

public enum ItemFrameDisplays {



    //Blocks rendered as Items

    DANDELION("minecraft:dandelion", "item", 0, 0, "/assets/mapsandframes/stationapi/textures/itemframe/items/dandelion.png"),
    BED("minecraft:bed", "item", 0, 0, "/assets/mapsandframes/stationapi/textures/itemframe/items/bed.png"),
    ROSE("minecraft:rose", "item", 0, 0, "/assets/mapsandframes/stationapi/textures/itemframe/items/rose.png"),
    RAIL("minecraft:rail", "item", 0, 0, "/assets/mapsandframes/stationapi/textures/itemframe/items/rail.png"),
    POWERED_RAIL("minecraft:powered_rail", "item", 0, 0, "/assets/mapsandframes/stationapi/textures/itemframe/items/powered_rail.png"),
    DETECTOR_RAIL("minecraft:detector_rail", "item", 0, 0, "/assets/mapsandframes/stationapi/textures/itemframe/items/detector_rail.png"),
    LADDER("minecraft:ladder", "item", 0, 0, "/assets/mapsandframes/stationapi/textures/itemframe/items/ladder.png"),
    COBWEB("minecraft:cobweb", "item", 0, 0, "/assets/mapsandframes/stationapi/textures/itemframe/items/cobweb.png"),
    BROWN_MUSHROOM("minecraft:brown_mushroom", "item", 0, 0, "/assets/mapsandframes/stationapi/textures/itemframe/items/brown_mushroom.png"),
    RED_MUSHROOM("minecraft:red_mushroom", "item", 0, 0, "/assets/mapsandframes/stationapi/textures/itemframe/items/red_mushroom.png"),
    OAK_SAPLING("minecraft:sapling", "item", 0, 0, "/assets/mapsandframes/stationapi/textures/itemframe/items/oak_sapling.png"),
    SPRUCE_SAPLING("minecraft:sapling", "item", 1, 0, "/assets/mapsandframes/stationapi/textures/itemframe/items/spruce_sapling.png"),
    BIRCH_SAPLING("minecraft:sapling", "item", 2, 0, "/assets/mapsandframes/stationapi/textures/itemframe/items/birch_sapling.png"),
    TORCH("minecraft:torch", "item", 0, 0, "/assets/mapsandframes/stationapi/textures/itemframe/items/torch.png"),
    REDSTONE_TORCH("minecraft:redstone_torch_lit", "item", 0, 0, "/assets/mapsandframes/stationapi/textures/itemframe/items/redstone_torch.png"),
    LEVER("minecraft:lever", "item", 0, 0, "/assets/mapsandframes/stationapi/textures/itemframe/items/lever.png"),

    TALL_GRASS("bhcreative:tall_grass", "item", 0, 0, "/assets/mapsandframes/stationapi/textures/itemframe/items/tall_grass.png"),
    FERN("bhcreative:fern", "item", 0, 0, "/assets/mapsandframes/stationapi/textures/itemframe/items/fern.png"),
    DEAD_BUSH("minecraft:dead_bush", "item", 0, 0, "/assets/mapsandframes/stationapi/textures/itemframe/items/dead_bush.png");


    private final String id;
    private final String type; // "block" or "item"
    private final int damage; // 0 if not used
    private final int variantCase; // 0 = all sides equal etc.
    private final String texturePath;
    private final boolean damageAgnostic;


    private ItemFrameDisplays(String id, String type, int damage, int variantCase, String texturePath){
        this.id = id;
        this.type = type;
        this.damage = damage;
        this.variantCase = variantCase;
        this.texturePath= texturePath;
        this.damageAgnostic = type.equals("tool");
    }

    public String getId() { return id; }
    public String getType() { return type; }
    public int getDamage() { return damage; }
    public int getVariantCase() { return variantCase; }
    public String getTexturePath() { return texturePath; }
    public boolean isDamageAgnostic() { return damageAgnostic; }

    public String getKey() {
        return (id + ":" + damage).toLowerCase();
    }
}
