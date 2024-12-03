package grill24.potionsplus.utility;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.HolderSetCodec;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.resources.ResourceKey;

import java.util.List;

public record HolderCodecs<E>(
        Codec<Holder<E>> holderCodec,
        Codec<HolderSet<E>> holderSetCodec,
        Codec<List<Holder<E>>> holderListCodec,
        StreamCodec<RegistryFriendlyByteBuf, Holder<E>> holderStreamCodec,
        StreamCodec<RegistryFriendlyByteBuf, HolderSet<E>> holderSetStreamCodec,
        StreamCodec<RegistryFriendlyByteBuf, List<Holder<E>>> holderListStreamCodec ) {

    public HolderCodecs(ResourceKey<? extends Registry<E>> registryKey, Codec<E> directCodec) {
        this(holder(registryKey, directCodec),
                holderSet(registryKey),
                holderList(registryKey, directCodec),
                holderStream(registryKey),
                holderSetStream(registryKey),
                holderListStream(registryKey));
    }

    // ----- Direct Codecs -----

    public static <E> Codec<Holder<E>> holder(ResourceKey<? extends Registry<E>> registryKey, Codec<E> directCodec) {
        return RegistryFileCodec.create(registryKey, directCodec);
    }

    public static <E> Codec<List<Holder<E>>> holderList(ResourceKey<? extends Registry<E>> registryKey, Codec<E> directCodec) {
        return ((HolderSetCodec<E>) HolderSetCodec.create(registryKey, holder(registryKey, directCodec), false)).homogenousListCodec;
    }

    public static <E> Codec<HolderSet<E>> holderSet(ResourceKey<? extends Registry<E>> registryKey) {
        return RegistryCodecs.homogeneousList(registryKey);
    }

    public static <E> Codec<ResourceKey<E>> resourceKey(ResourceKey<? extends Registry<E>> registryKey) {
        return ResourceKey.codec(registryKey);
    }

    // ----- Stream Codecs -----

    public static <E> StreamCodec<RegistryFriendlyByteBuf, Holder<E>> holderStream(ResourceKey<? extends Registry<E>> registryKey) {
        return ByteBufCodecs.holderRegistry(registryKey);
    }

    public static <E> StreamCodec<RegistryFriendlyByteBuf, List<Holder<E>>> holderListStream(ResourceKey<? extends Registry<E>> registryKey) {
        return holderStream(registryKey).apply(ByteBufCodecs.list());
    }

    public static <E> StreamCodec<RegistryFriendlyByteBuf, HolderSet<E>> holderSetStream(ResourceKey<? extends Registry<E>> registryKey) {
        return ByteBufCodecs.holderSet(registryKey);
    }

    public static <E> StreamCodec<ByteBuf, ResourceKey<E>> resourceKeyStream(ResourceKey<? extends Registry<E>> registryKey) {
        return ResourceKey.streamCodec(registryKey);
    }
}
