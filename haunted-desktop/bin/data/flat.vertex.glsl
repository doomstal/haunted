attribute vec4 a_position;
attribute vec2 a_texCoord0;

uniform float pos_x;
uniform float pos_y;
uniform float offset_u;
uniform float offset_v;
uniform mat4 u_projTrans;

varying vec2 v_texCoords;

void main() {
	v_texCoords = a_texCoord0 + vec2(offset_u, offset_v);
	gl_Position =  u_projTrans * (a_position + vec4(pos_x, pos_y, 0, 0));
}
