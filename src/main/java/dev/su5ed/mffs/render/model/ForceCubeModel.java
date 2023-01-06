package dev.su5ed.mffs.render.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.su5ed.mffs.render.ModRenderType;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

import static dev.su5ed.mffs.MFFSMod.location;

public class ForceCubeModel extends Model {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(location("force_cube"), "main");
    
    public static final ResourceLocation CORE_TEXTURE = location("textures/model/force_cube.png");
    public static final RenderType RENDER_TYPE = ModRenderType.STANDARD_TRANSLUCENT_ENTITY.apply(CORE_TEXTURE);

    private final ModelPart root;

    public ForceCubeModel(ModelPart root) {
		super(ModRenderType.STANDARD_TRANSLUCENT_ENTITY);

		this.root = root.getChild("root");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        partdefinition.addOrReplaceChild("root",
			CubeListBuilder.create()
				.texOffs(0, 0)
				.addBox(-8.0F, -8.0F, -8.0F, 16.0F, 16.0F, 16.0F),
			PartPose.offset(0.0F, 0.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 32);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        this.root.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}