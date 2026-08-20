package io.github.comodo811.mapsandframes.mixin;

import io.github.comodo811.mapsandframes.entity.ItemFrameEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(EntityRegistry.class)
abstract class EntityRegistryMixin {
    @Shadow
    private static void register(Class<? extends Entity> entityClass, String entityIdentifier, int entityId) {
    }

    @Shadow
    private static Map<String, Class<? extends Entity>> idToClass;

    @Shadow
    private static Map<Class<? extends Entity>, String> classToId;

    @Inject(method = "<clinit>", at = @At("RETURN"))
    private static void mapsandframes_registerItemFrame(CallbackInfo ci) {
        register(ItemFrameEntity.class, "mapsandframes:item_frame", 200);
        idToClass.put("mapsandframes:item_frame", ItemFrameEntity.class);
        classToId.put(ItemFrameEntity.class, "mapsandframes:item_frame");
    }
}
