package dev.su5ed.mffs.api.card;

import com.mojang.authlib.GameProfile;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.su5ed.mffs.util.ModUtil;
import io.netty.buffer.ByteBuf;
import net.minecraft.ChatFormatting;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

public sealed interface CardIdentity {
    Codec<CardIdentity> CODEC = Type.CODEC.dispatch(CardIdentity::type, Type::codec);
    StreamCodec<ByteBuf, CardIdentity> STREAM_CODEC = Type.STREAM_CODEC.dispatch(CardIdentity::type, Type::streamCodec);

    Type type();

    boolean matches(CardIdentity other);

    boolean matches(LivingEntity entity);

    Component getTooltip();

    Component getSimpleName();

    record PlayerCardIdentity(GameProfile profile) implements CardIdentity {
        public static final Codec<PlayerCardIdentity> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ExtraCodecs.AUTHLIB_GAME_PROFILE.fieldOf("profile").forGetter(PlayerCardIdentity::profile)
        ).apply(instance, PlayerCardIdentity::new));
        public static final StreamCodec<ByteBuf, PlayerCardIdentity> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.GAME_PROFILE,
            PlayerCardIdentity::profile,
            PlayerCardIdentity::new
        );

        @Override
        public Type type() {
            return Type.PLAYER;
        }

        @Override
        public boolean matches(CardIdentity other) {
            return other instanceof PlayerCardIdentity(GameProfile otherProfile) && this.profile.equals(otherProfile);
        }

        @Override
        public boolean matches(LivingEntity entity) {
            return entity instanceof Player player && this.profile.equals(player.getGameProfile());
        }

        @Override
        public Component getTooltip() {
            return ModUtil.translate("info", "identity", getSimpleName())
                .withStyle(ChatFormatting.DARK_GRAY);
        }

        @Override
        public Component getSimpleName() {
            return Component.literal(this.profile.name()).withStyle(ChatFormatting.GREEN);
        }
    }

    record EntityCardIdentity(@Nullable String name, String typeDescriptionId, UUID uuid) implements CardIdentity {
        public static final Codec<EntityCardIdentity> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.optionalFieldOf("name").forGetter(e -> Optional.ofNullable(e.name)),
            Codec.STRING.fieldOf("typeDescriptionId").forGetter(EntityCardIdentity::typeDescriptionId),
            UUIDUtil.STRING_CODEC.fieldOf("profile").forGetter(EntityCardIdentity::uuid)
        ).apply(instance, EntityCardIdentity::new));
        public static final StreamCodec<ByteBuf, EntityCardIdentity> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.optional(ByteBufCodecs.STRING_UTF8),
            e -> Optional.ofNullable(e.name),
            ByteBufCodecs.STRING_UTF8,
            EntityCardIdentity::typeDescriptionId,
            UUIDUtil.STREAM_CODEC,
            EntityCardIdentity::uuid,
            EntityCardIdentity::new
        );

        private EntityCardIdentity(Optional<String> name, String typeDescriptionId, UUID uuid) {
            this(name.orElse(null), typeDescriptionId, uuid);
        }

        @Override
        public Type type() {
            return Type.ENTITY;
        }

        @Override
        public boolean matches(CardIdentity other) {
            return other instanceof EntityCardIdentity entity && this.uuid.equals(entity.uuid());
        }

        @Override
        public boolean matches(LivingEntity entity) {
            return this.uuid.equals(entity.getUUID());
        }

        @Override
        public Component getTooltip() {
            return ModUtil.translate("info", "identity", getDisplayName(true))
                .withStyle(ChatFormatting.DARK_GRAY);
        }

        @Override
        public Component getSimpleName() {
            return getDisplayName(false);
        }

        private Component getDisplayName(boolean color) {
            return ModUtil.translate("info", "entity_identity",
                Component.literal(this.name != null ? this.name : this.uuid.toString()).withStyle(ChatFormatting.AQUA),
                Component.translatable(this.typeDescriptionId)
            ).withStyle(color ? ChatFormatting.GRAY : ChatFormatting.WHITE);
        }
    }

    enum Type implements StringRepresentable {
        PLAYER(PlayerCardIdentity.CODEC, PlayerCardIdentity.STREAM_CODEC),
        ENTITY(EntityCardIdentity.CODEC, EntityCardIdentity.STREAM_CODEC);

        static final Codec<Type> CODEC = StringRepresentable.fromEnum(Type::values);
        static final StreamCodec<ByteBuf, Type> STREAM_CODEC = ByteBufCodecs.idMapper(id -> values()[id], Type::ordinal);

        private final Codec<? extends CardIdentity> codec;
        private final StreamCodec<? super ByteBuf, ? extends CardIdentity> streamCodec;

        Type(Codec<? extends CardIdentity> codec, StreamCodec<? super ByteBuf, ? extends CardIdentity> streamCodec) {
            this.codec = codec;
            this.streamCodec = streamCodec;
        }

        public MapCodec<? extends CardIdentity> codec() {
            return this.codec.fieldOf("data");
        }

        public StreamCodec<? super ByteBuf, ? extends CardIdentity> streamCodec() {
            return this.streamCodec;
        }

        @Override
        public String getSerializedName() {
            return name();
        }
    }
}
