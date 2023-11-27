package com.springframework.asm;

/**
 * A reference to a field or a method.
 *
 * @author Remi Forax
 * @author Eric Bruneton
 */
public final class Handle {


    private final int tag;

    /** The internal name of the class that owns the field or method designated by this handle. */
    private final String owner;

    /** The name of the field or method designated by this handle. */
    private final String name;

    /** The descriptor of the field or method designated by this handle. */
    private final String descriptor;

    /** Whether the owner is an interface or not. */
    private final boolean isInterface;


    @Deprecated
    public Handle(final int tag, final String owner, final String name, final String descriptor) {
        this(tag, owner, name, descriptor, tag == Opcodes.H_INVOKEINTERFACE);
    }


    public Handle(
            final int tag,
            final String owner,
            final String name,
            final String descriptor,
            final boolean isInterface) {
        this.tag = tag;
        this.owner = owner;
        this.name = name;
        this.descriptor = descriptor;
        this.isInterface = isInterface;
    }


    public int getTag() {
        return tag;
    }

    /**
     * Returns the internal name of the class that owns the field or method designated by this handle.
     *
     * @return the internal name of the class that owns the field or method designated by this handle.
     */
    public String getOwner() {
        return owner;
    }

    /**
     * Returns the name of the field or method designated by this handle.
     *
     * @return the name of the field or method designated by this handle.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the descriptor of the field or method designated by this handle.
     *
     * @return the descriptor of the field or method designated by this handle.
     */
    public String getDesc() {
        return descriptor;
    }

    /**
     * Returns true if the owner of the field or method designated by this handle is an interface.
     *
     * @return true if the owner of the field or method designated by this handle is an interface.
     */
    public boolean isInterface() {
        return isInterface;
    }

    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (!(object instanceof Handle)) {
            return false;
        }
        Handle handle = (Handle) object;
        return tag == handle.tag
                && isInterface == handle.isInterface
                && owner.equals(handle.owner)
                && name.equals(handle.name)
                && descriptor.equals(handle.descriptor);
    }

    @Override
    public int hashCode() {
        return tag
                + (isInterface ? 64 : 0)
                + owner.hashCode() * name.hashCode() * descriptor.hashCode();
    }

    /**
     * Returns the textual representation of this handle. The textual representation is:
     *
     * <ul>
     *   <li>for a reference to a class: owner "." name descriptor " (" tag ")",
     *   <li>for a reference to an interface: owner "." name descriptor " (" tag " itf)".
     * </ul>
     */
    @Override
    public String toString() {
        return owner + '.' + name + descriptor + " (" + tag + (isInterface ? " itf" : "") + ')';
    }
}
