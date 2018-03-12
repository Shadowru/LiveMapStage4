#version 300 es

uniform lowp vec4       vMaterialSpecular;
uniform lowp vec3       vMaterialDiffuse;

uniform highp vec3      vLight0;

in lowp vec3 dynamicDiffuse;
in mediump vec3 eye;
in highp vec3 normal;
in highp vec3 halfvecLight0;
 	
uniform samplerCube sCubemapTexture;
uniform lowp vec2	vRoughness;

out mediump vec4 fragmentColor;
#define M_PI 3.1415926535897932384626433832795

lowp vec3 FresnelSchlickWithRoughness(lowp vec3 SpecularColor, lowp vec3 E, lowp vec3 N, lowp float Gloss)
{
	return SpecularColor + (max(vec3(Gloss,Gloss,Gloss), SpecularColor) - SpecularColor) * pow(1.0 - clamp(dot(E, N), 0.0, 1.0), 5.0);
}

void main()
{
	mediump float MipmapIndex = vRoughness.x * vRoughness.y;

	lowp vec3 diffuseEnvColor = textureLod(sCubemapTexture, normal, MipmapIndex).xyz * vMaterialDiffuse / M_PI;

    lowp vec3 eyeNormalized = normalize(eye);
    lowp vec3 reflection = reflect(-eyeNormalized, normal);
    lowp float fresnelTerm = pow(1.0 - clamp(dot(eyeNormalized, halfvecLight0), 0.0, 1.0), 5.0);

	highp float NdotH = max(dot(normalize(normal), normalize(halfvecLight0)), 0.0);
    lowp float fPower = exp2(10.0 * (1.0 - vRoughness.x) + 1.0);
    lowp float dynamicSpecular = pow(NdotH, fPower);
	mediump float normalizeSpecular = (fPower + 1.0) / (M_PI * 2.0);

	dynamicSpecular = (dynamicSpecular + fresnelTerm) * normalizeSpecular;

	lowp vec3 fresnel = FresnelSchlickWithRoughness(vMaterialSpecular.xyz, eyeNormalized, normal, 1.0 - vRoughness.x);
	lowp vec3 specularEnvColor = textureLod(sCubemapTexture, reflection, MipmapIndex).xyz * fresnel;

	fragmentColor = vec4(dynamicSpecular * vMaterialSpecular.xyz + dynamicDiffuse
					+ diffuseEnvColor + specularEnvColor, 1.0);

}