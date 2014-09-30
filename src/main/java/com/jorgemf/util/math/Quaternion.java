package com.jorgemf.util.math;

/**
 * A Quaternion class, it is a representation of 3D rotations which avoid artifacts when operate with them.
 *
 * @link http://content.gpwiki.org/index.php/OpenGL:Tutorials:Using_Quaternions_to_represent_rotation
 */
public class Quaternion {

    private final static float TOLERANCE = 0.00001f;
    private final static float PIOVER180 = (float) (Math.PI / 180);
    private static Vector3f auxVector = new Vector3f();// used for calculating the quaternion rotation given two vectors
    private float x;
    private float y;
    private float z;
    private float w;
    private float[] matrix;
    private boolean updateMatrix;

    /**
     * Default constructor with no rotation
     */
    public Quaternion() {
        this(0, 0, 0, 1);
    }

    /**
     * Copy constructor
     *
     * @param q other quaternion
     */
    public Quaternion(Quaternion q) {
        this(q.x, q.y, q.z, q.w);
        if (!q.updateMatrix) {
            System.arraycopy(q.matrix, 0, this.matrix, 0, q.matrix.length);
            this.updateMatrix = false;
        } else {
            this.updateMatrix = true;
        }
    }

    public Quaternion(float x, float y, float z, float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
        this.matrix = new float[16];
        normalise();
        this.updateMatrix = true;
    }

    public final float getX() {
        return x;
    }

    public final float getY() {
        return y;
    }

    public final float getZ() {
        return z;
    }

    public final float getW() {
        return w;
    }

    public final void clear() {
        this.x = 0;
        this.y = 0;
        this.z = 0;
        this.w = 1;
        this.updateMatrix = true;
    }

    // normalising a quaternion works similar to a vector. This method will not do anything
    // if the quaternion is close enough to being unit-length. define TOLERANCE as something
    // small like 0.00001f to get accurate results
    private void normalise() {
        // Don't normalize if we don't have to
        float mag2 = w * w + x * x + y * y + z * z;
        if (Math.abs(mag2) > TOLERANCE && Math.abs(mag2 - 1.0f) > TOLERANCE) {
            float mag = (float) Math.sqrt(mag2);
            w /= mag;
            x /= mag;
            y /= mag;
            z /= mag;
        }
    }

    public final Quaternion getConjugate() {
        return new Quaternion(-x, -y, -z, w);
    }

    public final void conjugate() {
        this.x = -this.x;
        this.y = -this.y;
        this.z = -this.z;
        this.updateMatrix = true;
    }

    // Multiplying q1 with q2 applies the rotation q2 to q1
    public final void rotate(Quaternion rq) {
        float newX = w * rq.x + x * rq.w + y * rq.z - z * rq.y;
        float newY = w * rq.y + y * rq.w + z * rq.x - x * rq.z;
        float newZ = w * rq.z + z * rq.w + x * rq.y - y * rq.x;
        float newW = w * rq.w - x * rq.x - y * rq.y - z * rq.z;

        this.x = newX;
        this.y = newY;
        this.z = newZ;
        this.w = newW;
        this.updateMatrix = true;
        normalise();
    }

    //http://en.wikipedia.org/wiki/Conversion_between_quaternions_and_Euler_angles
    /*
    public float getXAngleRadians(){
		return (float)Math.atan2(2*(w*x+y*z), 1-2*(x*x+y*y));
	}
	
	public float getYAngleRadians(){
		return (float)Math.asin(2*(w*y-x*z));
	}
	
	public float getZAngleRadians(){
		return (float)Math.atan2(2*(w*z+y*x), 1-2*(z*z+y*y));
	}
	*/

    public final void setFromEulerXYZDegrees(float x, float y, float z) {
        setFromEulerXYZRadians(x * PIOVER180 / 2, y * PIOVER180 / 2, z * PIOVER180 / 2);
    }

    public final void setFromEulerPWRdegress(float roll, float pitch, float yaw) {
        setFromEulerXYZRadians(roll * PIOVER180 / 2, pitch * PIOVER180 / 2, yaw * PIOVER180 / 2);
    }

    public final void setFromEulerPWRradians(float roll, float pitch, float yaw) {
        setFromEulerXYZRadians(roll, pitch, yaw);
    }

    public final void setFromEulerXYZRadians(float rx, float ry, float rz) {
        // Convert from Euler Angles
        // Basically we create 3 Quaternions, one for pitch, one for yaw, one for roll
        // and multiply those together.The calculation below does the same, just shorter

        float r = rx;
        float p = ry;
        float y = rz;

        float sinp = (float) Math.sin(p);
        float siny = (float) Math.sin(y);
        float sinr = (float) Math.sin(r);
        float cosp = (float) Math.cos(p);
        float cosy = (float) Math.cos(y);
        float cosr = (float) Math.cos(r);

        this.x = sinr * cosp * cosy - cosr * sinp * siny;
        this.y = cosr * sinp * cosy + sinr * cosp * siny;
        this.z = cosr * cosp * siny - sinr * sinp * cosy;
        this.w = cosr * cosp * cosy + sinr * sinp * siny;
        this.normalise();
        this.updateMatrix = true;
    }


    /**
     * Returns a rotation matrix which represents the quaternion
     *
     * @return a rotation matrix which represents the quaternion
     */
    public final float[] getMatrix() {
        if (this.updateMatrix) {
            calculateMatrix();
            this.updateMatrix = false;
        }
        return this.matrix;
    }

    private void calculateMatrix() {
        float x2 = x * x;
        float y2 = y * y;
        float z2 = z * z;
        float xy = x * y;
        float xz = x * z;
        float yz = y * z;
        float wx = w * x;
        float wy = w * y;
        float wz = w * z;

        // This calculation would be a lot more complicated for non-unit length quaternions
        this.matrix[0] = 1.0f - 2.0f * (y2 + z2);
        this.matrix[1] = 2.0f * (xy - wz);
        this.matrix[2] = 2.0f * (xz + wy);
        this.matrix[3] = 0.0f;
        this.matrix[4] = 2.0f * (xy + wz);
        this.matrix[5] = 1.0f - 2.0f * (x2 + z2);
        this.matrix[6] = 2.0f * (yz - wx);
        this.matrix[7] = 0.0f;
        this.matrix[8] = 2.0f * (xz - wy);
        this.matrix[9] = 2.0f * (yz + wx);
        this.matrix[10] = 1.0f - 2.0f * (x2 + y2);
        this.matrix[11] = 0.0f;
        this.matrix[12] = 0.0f;
        this.matrix[13] = 0.0f;
        this.matrix[14] = 0.0f;
        this.matrix[15] = 1.0f;
    }

    public final boolean isEquals(float x, float y, float z, float w) {
        return this.x == x && this.y == y && this.z == z && this.w == w;
    }

    public final boolean isEquals(Quaternion q) {
        return this.x == q.x && this.y == q.y && this.z == q.z && this.w == q.w;
    }

    public final void setValues(float x, float y, float z, float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
        normalise();
        this.updateMatrix = true;
    }

    public final void set(Quaternion q) {
        this.x = q.x;
        this.y = q.y;
        this.z = q.z;
        this.w = q.w;
        if (!q.updateMatrix) {
            System.arraycopy(q.matrix, 0, this.matrix, 0, q.matrix.length);
            this.updateMatrix = false;
        } else {
            this.updateMatrix = true;
        }
    }

    /**
     * Set the angle rotation between two vectors. Use with caution, returns the fastest direction of rotation. If u=-v the rotation is returned in z axis.
     *
     * @link http://stackoverflow.com/questions/1171849/finding-quaternion-representing-the-rotation-from-one-vector-to-another
     */
    public final void setAngle(Vector3f u, Vector3f v) {
        float dot = u.dot(v);
        float lengths = (float) Math.sqrt(u.length2() * v.length2());
        float angle = dot / lengths;
        if (angle >= (1 - TOLERANCE)) {
            // parallel vectors, null angle
            this.x = 0;
            this.y = 0;
            this.z = 0;
            this.w = 1;
        } else if (angle <= (TOLERANCE - 1)) {
            this.x = 0;
            this.y = 0; // return a 180º rotation in z axis
            this.z = 1;
            this.w = 0;
        } else {
            this.w = lengths + dot;
            auxVector.cross(v, u);
            this.x = auxVector.x;
            this.y = auxVector.y;
            this.z = auxVector.z;
            normalise();
        }
        this.updateMatrix = true;
    }

    public String toString() {
        return "[x:" + x + ",y:" + y + ",z:" + z + ",w:" + w + "]";
    }

}
