package io.github.comodo811.mapsandframes.entity.renderer;


import net.modificationstation.stationapi.api.client.render.RendererAccess;
import net.modificationstation.stationapi.api.client.render.model.BakedModelRenderer;

import java.util.Objects;

public final class PublicRendererHolder {
    private PublicRendererHolder() { throw new RuntimeException("No"); }

    public final static BakedModelRenderer RENDERER = Objects.requireNonNull(RendererAccess.INSTANCE.getRenderer()).bakedModelRenderer();
}
