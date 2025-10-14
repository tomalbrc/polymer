package eu.pb4.polymer.virtualentity.mixin;

import eu.pb4.polymer.virtualentity.api.attachment.HolderAttachment;
import eu.pb4.polymer.virtualentity.impl.EntityExt;
import eu.pb4.polymer.virtualentity.impl.HolderAttachmentHolder;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.Collection;
import java.util.Collections;

@Mixin(Entity.class)
public abstract class EntityMixin implements HolderAttachmentHolder, EntityExt {
    @Unique
    private final Collection<HolderAttachment> polymerVE$holders = Collections.synchronizedList(new ObjectArrayList<>());
    @Unique
    private final IntList polymerVE$virtualRidden = new IntArrayList();
    @Unique
    private boolean polymerVE$virtualRiddenDirty = false;

    @Override
    public void polymerVE$addHolder(HolderAttachment holderAttachment) {
        this.polymerVE$holders.add(holderAttachment);
    }

    @Override
    public void polymerVE$removeHolder(HolderAttachment holderAttachment) {
        this.polymerVE$holders.remove(holderAttachment);
    }

    @Override
    public Collection<HolderAttachment> polymerVE$getHolders() {
        return this.polymerVE$holders;
    }

    @Override
    public IntList polymerVE$getVirtualRidden() {
        return this.polymerVE$virtualRidden;
    }

    @Override
    public void polymerVE$markVirtualRiddenDirty() {
        this.polymerVE$virtualRiddenDirty = true;
    }

    @Override
    public boolean polymerVE$getAndClearVirtualRiddenDirty() {
        var old = this.polymerVE$virtualRiddenDirty;
        this.polymerVE$virtualRiddenDirty = false;
        return old;
    }
}
