/*
 * Based on "Airsolid Glitch color 02" by airsolid.
 * Licensed under the Creative Commons Attribution-NonCommercial-ShareAlike 3.0 Unported License as stated in https://www.shadertoy.com/terms.
 * Original code available at https://www.shadertoy.com/view/ttdXD8.
 */
#version 150

#define RGB_INTENSITY 0.15
#define DISPLACE_INTENSITY 0.1
#define INTERLACE_INTENSITY 0.01
#define DROPOUT_INTENSITY 0.01
#define DIST_MODIFIER 3.0
#define INTERVAL 0.9

uniform sampler2D DiffuseSampler;
uniform sampler2D NoiseSampler;
uniform sampler2D DepthSampler;

uniform vec4 ColorModulate;
uniform mat4 ProjMat;
uniform float Time;

in vec2 texCoord;

out vec4 fragColor;

float rand(float n)
{
    return fract(sin(n) * 43758.5453123);
}

float noise(float p)
{
    float fl = floor(p);
    float fc = fract(p);
    return mix(rand(fl), rand(fl + 1.0), fc);
}

float blockyNoise(vec2 uv, float threshold, float scale, float seed)
{
    float scroll = floor(Time + sin(11.0 *  Time) + sin(Time)) * 0.77;
    vec2 noiseUV = uv.yy / scale + scroll;
    float noise2 = texture(NoiseSampler, noiseUV).r;

    float id = floor( noise2 * 20.0);
    id = noise(id + seed) - 0.5;

    if (abs(id) > threshold) {
        id = 0.0;
    }

    return id;
}

void glitch()
{
    float rgbIntesnsity = RGB_INTENSITY + 0.1 * sin(Time * 3.7);
    float displaceIntesnsity = DISPLACE_INTENSITY + 0.3 * pow(sin(Time * 1.2), 5.0);
    vec2 uv = texCoord;

    float displace = blockyNoise(uv + vec2(uv.y, 0.0), displaceIntesnsity, 25.0, 66.6);
    displace *= blockyNoise(uv.yx + vec2(0.0, uv.x), displaceIntesnsity, 111.0, 13.7);

    float distMultiplier = 1.0 - texture(DepthSampler, texCoord).r;
    uv.x += displace * (distMultiplier * DIST_MODIFIER);

    vec2 offs = 0.01 * vec2(blockyNoise(uv.xy + vec2(uv.y, 0.0), rgbIntesnsity, 65.0, 341.0), 0.0);

    float colr = texture(DiffuseSampler, uv - offs).r;
    float colg = texture(DiffuseSampler, uv).g;
    float colb = texture(DiffuseSampler, uv + offs).b;

    float line = fract(gl_FragCoord.y / 3.0);
    vec3 mask = vec3(3.0, 0.0, 0.0);
    if (line > 0.333) {
        mask = vec3(0.0, 3.0, 0.0);
    }
    if (line > 0.666) {
        mask = vec3(0.0, 0.0, 3.0);
    }

    float maskNoise = 1.0 - blockyNoise(uv, INTERLACE_INTENSITY, 90.0, Time) * max(displace, offs.x);
    if (maskNoise == 1.0) {
        mask = vec3(1.0);
    }

    float dropout = blockyNoise(uv, DROPOUT_INTENSITY, 11.0, Time) * blockyNoise(uv.yx, DROPOUT_INTENSITY, 90.0, Time);
    mask *= (1.0 - 5.0 * dropout);

    fragColor = vec4(mask * vec3(colr, colg, colb), texture(DiffuseSampler, uv).a) * ColorModulate;
    gl_FragDepth = texture(DepthSampler, uv).r;
}

void main()
{
    if (rand(Time) >= INTERVAL)
    {
        glitch();
    }
    else
    {
        fragColor = texture(DiffuseSampler, texCoord) * ColorModulate;
        gl_FragDepth = texture(DepthSampler, texCoord).r;
    }
}