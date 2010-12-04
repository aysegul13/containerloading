/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Model;

/**
 *
 * @author otavio_zabaleta
 */
public class Box {

    //Box dimensions
    public Vector3d sides;
    //se a caixa está dentro do container
    public boolean isInside;
    //se a caixa está rotacionada
    public boolean isRotated;
    //Idicates if side can be placed horizontally
    public boolean xv,yv,zv;
    public int volume;
    //Reflects the sides os of the box according to it's orientation
    public int relativeDimensions[];
    public Vector3d relativeCoordenates;

    public Orientation vSide;

    //Class constructor
    public Box(Vector3d _sides, boolean _xv, boolean _yv, boolean _zv)
    {
        //Set sides
        this.sides = new Vector3d(_sides.x, _sides.y, _sides.z);
        this.relativeDimensions = new int[3];

        //Default vertical orientation == z
        this.vSide = Orientation.z;
        //quando criada, a caixa esta com os sides originais
        this.isRotated = false;
        //quando criada, a caixa esta fora do container
        this.isInside = false;
        //dimensoes relativas ao container
        this.relativeDimensions[0] = this.sides.x;
        this.relativeDimensions[1] = this.sides.y;
        this.relativeDimensions[2] = this.sides.z;

        //coordenadas da caixa dentro do container, quando inserida.
        this.relativeCoordenates = new Vector3d(0, 0, 0);

        //volume da caixa
        this.volume = this.sides.x * this.sides.y * this.sides.z;
        //Set vertical orientation possibilities
        this.xv = _xv;
        this.yv = _yv;
        this.zv = _zv;
    }

    //Checks if a box axis can be placed vertically
    public boolean canFlip(Orientation or)
    {
        switch(or)
        {
            case x:
            {
                if(this.xv)
                    return true;
                else
                    return false;
            }
            case y:
            {
                if(this.yv)
                    return true;
                else
                    return false;
            }
            case z:
            {
                if(this.zv)
                    return true;
                else
                    return false;
            }
        }
        return false;
    }

    //Changes the relativeDimensions[] values to reflects the box vertical orientation
    public void flip(Orientation or)
    {
        if (this.vSide == or)
            return;

        this.vSide = or;
        switch(or)
        {
            case  x:
            {
                this.relativeDimensions[0] = this.sides.z;
                this.relativeDimensions[1] = this.sides.y;
                this.relativeDimensions[2] = this.sides.x;
                break;
            }
            case y:
            {
                this.relativeDimensions[0] = this.sides.x;
                this.relativeDimensions[1] = this.sides.z;
                this.relativeDimensions[2] = this.sides.y;
                break;
            }
            case z:
            {
                this.relativeDimensions[0] = this.sides.x;
                this.relativeDimensions[1] = this.sides.y;
                this.relativeDimensions[2] = this.sides.z;
                break;
            }
        }
    }

    /*
     * Rotates a box 90° around container z axis
     */
    public void rotate()
    {
        int aux = this.relativeDimensions[0];
        this.relativeDimensions[0] = this.relativeDimensions[1];
        this.relativeDimensions[1] = aux;
        this.isRotated = !this.isRotated;
    }

}