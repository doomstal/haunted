#ifdef GL_ES
#define LOWP lowp
precision mediump float;
#else
#define LOWP
#endif

varying vec2 v_texCoords;

uniform sampler2D u_texture;
uniform float ambient_intensity;

void main() {
	vec4 texture_texel = texture2D(u_texture, v_texCoords);
	if(texture_texel.a < 0.01) discard;
	float light = ambient_intensity;
	gl_FragColor = texture_texel * light;
}
