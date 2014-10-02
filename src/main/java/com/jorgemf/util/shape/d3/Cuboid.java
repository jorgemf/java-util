package com.jorgemf.util.shape.d3;

import com.jorgemf.util.math.Vector3f;

public interface Cuboid {

    /**
     * TODO set the points in the correct order
     * <p/>
     * <pre>
     *        #------#
     *       /|     /|
     *      / |    / |
     *     #--+---#  |
     *     |  #---+--#
     *     | /    | /
     *     |/     |/
     *     #------#
     * </pre>
     *
     * @return
     */
    public Vector3f[] getPoints();
}
