package net.liukrast.santa;

import net.minecraft.world.phys.Vec2;

public record Node(Vec2 pos, Vec2 direction) {
    public Node(float aX, float aZ, float bX, float bZ) {
        this(new Vec2(aX, aZ), new Vec2(bX, bZ));
    }
}
