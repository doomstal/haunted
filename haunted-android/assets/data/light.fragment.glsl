#ifdef GL_ES
#define LOWP lowp
precision mediump float;
#else
#define LOWP
#endif

varying vec2 v_texCoords;
varying vec3 v_position;
varying vec3 v_normal;

uniform sampler2D u_texture;
uniform sampler2D u_normal;

uniform vec3 light_position;
uniform vec3 light_color;
uniform float light_radius;
uniform float light_intensity;

uniform int cel_shading_steps;

uniform bool has_normals;

void main() {

	vec4 texture_texel = texture2D(u_texture, v_texCoords);
	if(texture_texel.a < 0.01) discard;

	vec3 vertex_position = v_position;

	vec3 light_dir = light_position - vertex_position;

	vec4 normal_texel = texture2D(u_normal, v_texCoords);
	if(abs(vertex_position.z) < 0.01) {
		light_dir.z = 0.0;
		normal_texel.b = 0.5;
	}

	float dist = length(light_dir);
	if(dist > light_radius*2.0) discard;

	vec3 normal = normal_texel.rgb * 2.0 - 1.0;
	if(has_normals) normal = v_normal;
	if(length(normal) > 1.0) normal = normalize(normal);

	float r = 1.0 + (dist/light_radius);
	float attenuation = 1.0 / (r*r);

	if(dist > light_radius) {
		attenuation *= 1.0 - (dist - light_radius) / light_radius;
	}

	light_dir /= dist;
	float diffuse = max(0.0, dot(normal, light_dir));

	//attenuation = max(attenuation, 1.0);
	//diffuse = max(diffuse, 1.0);

	float light = diffuse * light_intensity * attenuation * normal_texel.a;

	if(cel_shading_steps > 0) light = floor(light * float(cel_shading_steps) + 0.5) / float(cel_shading_steps);

	gl_FragColor = vec4(texture_texel.rgb * light_color * light, 1.0);
}
