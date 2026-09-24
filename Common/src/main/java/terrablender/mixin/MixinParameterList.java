/**
 * Copyright (C) Glitchfiend
 * <p>
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package terrablender.mixin;

import net.minecraft.core.RegistryAccess;
import net.minecraft.world.level.biome.Climate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import terrablender.api.Region;
import terrablender.api.RegionType;
import terrablender.worldgen.IExtendedParameterList;

/** Passive extension; there are no TerraBlender region trees or per-coordinate noise. */
@Mixin(Climate.ParameterList.class)
public abstract class MixinParameterList<T> implements IExtendedParameterList<T> {
    @Shadow public abstract T findValue(Climate.TargetPoint target);
    @Override public void initializeForTerraBlender(RegistryAccess registries, RegionType type, long seed) {}
    @Override public boolean isInitialized() { return false; }
    @Override public int getTreeCount() { return 0; }
    @Override public int getUniqueness(int x, int y, int z) { return 0; }
    @Override public Climate.RTree getTree(int uniqueness) { return null; }
    @Override public Region getRegion(int uniqueness) { return null; }
    @Override public T findValuePositional(Climate.TargetPoint target, int x, int y, int z) {
        return this.findValue(target);
    }
    @Override public void recreateUniqueness() {}
    @Override public Climate.ParameterList<T> clone() {
        try { return (Climate.ParameterList<T>) super.clone(); }
        catch (CloneNotSupportedException e) { throw new AssertionError(e); }
    }
}
