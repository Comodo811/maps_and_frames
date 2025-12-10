package io.github.comodo811.mapsandframes.display;

public enum ItemFrameDisplays {



    //Blocks rendered as Items

    DANDELION("minecraft:dandelion", "item", 0),
    BED("minecraft:bed", "item", 0),
    ROSE("minecraft:rose", "item", 0),
    RAIL("minecraft:rail", "item", 0),
    POWERED_RAIL("minecraft:powered_rail", "item", 0),
    DETECTOR_RAIL("minecraft:detector_rail", "item", 0),
    LADDER("minecraft:ladder", "item", 0 ),
    COBWEB("minecraft:cobweb", "item", 0),
    BROWN_MUSHROOM("minecraft:brown_mushroom", "item", 0),
    RED_MUSHROOM("minecraft:red_mushroom", "item", 0),
    OAK_SAPLING("minecraft:sapling", "item", 0),
    SPRUCE_SAPLING("minecraft:sapling", "item", 1),
    BIRCH_SAPLING("minecraft:sapling", "item", 2),
    TORCH("minecraft:torch", "item", 0),
    REDSTONE_TORCH("minecraft:redstone_torch_lit", "item", 0),
    LEVER("minecraft:lever", "item", 0),

    TALL_GRASS("bhcreative:tall_grass", "item", 0),
    FERN("bhcreative:fern", "item", 0),
    DEAD_BUSH("minecraft:dead_bush", "item", 0);


    private final String id;
    private final String type; // "block" or "item"
    private final int damage; // 0 if not used
    private final boolean damageAgnostic;


    private ItemFrameDisplays(String id, String type, int damage){
        this.id = id;
        this.type = type;
        this.damage = damage;
        this.damageAgnostic = type.equals("tool");
    }

    public String getId() { return id; }
    public String getType() { return type; }
    public int getDamage() { return damage; }
    public boolean isDamageAgnostic() { return damageAgnostic; }

    public String getKey() {
        return (id + ":" + damage).toLowerCase();
    }
}
