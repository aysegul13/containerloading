/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

/**
 *
 * @author otavio_zabaleta
 */
public class Container {
    //The container is divided in 1cm side cubes.

    public boolean spMatrix[][][];
    private int x, y, z;
    public int volume;

    public Container(Vector3d _size) {
        this.x = _size.x;
        this.y = _size.y;
        this.z = _size.z;
        this.volume = _size.x * _size.y * _size.z;
        spMatrix = new boolean[x][y][z];
        //inicializa toda a matriz do container com 0 (container vazio)
        for (int i = 0; i < x; i++) {
            for (int j = 0; j < y; j++) {
                for (int k = 0; k < z; k++) {
                    spMatrix[i][j][k] = false;
                }
            }
        }
    }

    //Add a box
    public void Add(Box bx) {
    }

    //Remove a box
    public void Remove(Box bx) {
    }

    //Checks if the bottom of a box hits something
    private boolean BottomOverlap(int x, int y, Vector3d _pos) {
        int xMax = _pos.x + x;
        int yMax = _pos.y + y;

        try {
            if (xMax > this.x || yMax > this.y) {
                throw new Exception("bottomOverlap(int x, int y, Vector3d _pos) - Porra, saiu fora do container!");
            }

            for (int i = _pos.x; i < xMax; i++) {
                for (int j = _pos.y; j < yMax; j++) {
                    if (this.spMatrix[i][j][_pos.z]) {
                        return true;
                    }
                }
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

        return false;
    }

    /* Makes a box fall to the lowest z of it's current (x,y) position.
     * PRECONDITIONS: box position must be a valid one (e.g. no space
     * overlapping and no container boundary exceeded)
     */
    public Vector3d FallBox(Box bx, Vector3d _pos) {
        Vector3d pos = _pos;
        try {
            if (pos.z < 0) {
                throw new Exception("FallBox(Box bx, Vector3d _pos) - Porra, saiu fora do container!");
            }
            int upperRef = pos.z;
            int currentRef = upperRef;
            int lowerRef = 0;

            while (true) {
                if (BottomOverlap(bx.relativeDimensions[0], bx.relativeDimensions[1], pos)) {
                    lowerRef = pos.z;
                    currentRef = (lowerRef + upperRef) / 2;
                    pos.z = currentRef;
                } else {
                    upperRef = pos.z;
                    currentRef = (lowerRef + upperRef) / 2;
                    pos.z = currentRef;
                }

                if (upperRef - lowerRef == 1) {
                    pos.z++;
                    break;
                } else if (upperRef == lowerRef) {
                    pos.z++;
                    break;
                }
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

        return pos;
    }

    public Vector3d getPosition(Box bx) {
        //min(x,y), max(z)
        Vector3d pos = new Vector3d(0, 0, this.z - bx.relativeDimensions[2]);

        /*TODO
         *
         * Create logic to find a position to the box. I suggest we use a
         * lefter/deeper aproach (min x,y) after what we can make the box
         * fall to the lower z (min z).
         *
         * Ao invés de
         */

        return pos;
    }

    /*
     * It basically check if the box bottom overlaps any occupied block.
     * Since all the boxes are placed in the ceiling at first, there is no need
     * PRECONDITIONS: the box position must be such as to make it touch the
     * ofceiling of the container.
     * (pos.z + vertical side value = container.z + 1)
     */
    public boolean fitsHere(Box bx, Vector3d pos) {
        try {
            //First checks if any container boundary is exceeded
            if ((pos.x + bx.relativeDimensions[0]) >= this.x) {
                throw new Exception("fitsHere(Box bx, Vector3d _pos) - Porra, saiu fora do container!");
            }
            if ((pos.y + bx.relativeDimensions[1]) >= this.y) {
                throw new Exception("fitsHere(Box bx, Vector3d _pos) - Porra, saiu fora do container!");
            }
            if ((pos.z + bx.relativeDimensions[2]) >= this.z) {
                throw new Exception("fitsHere(Box bx, Vector3d _pos) - Porra, saiu fora do container!");
            }

            if (BottomOverlap(bx.relativeDimensions[0], bx.relativeDimensions[0], pos)) {
                return false;
            }


            //Brute force check
            for (int i = pos.x; i < pos.x + bx.relativeDimensions[0]; i++) {
                for (int j = pos.y; j < pos.y + bx.relativeDimensions[1]; j++) {
                    for (int k = pos.z; k < pos.z + bx.relativeDimensions[2]; k++) {
                        if (this.spMatrix[i][j][k]) {
                            return false;
                        }
                    }
                }
            }

        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return true;
    }

    /**
     * Testa se uma caixa cabe dentro do container, em algum lugar.
     * Inicia a procura pelo canto inferior esquerdo e vai progradindo
     * primeiro em x (largura), depois em y (comprimento) e então em z (altura).
     */
    public Box fitsIn(Box _box, Vector3d lastBoxInserted) {
        //Varre shuazenegeriamente procurando um lugar pra colocar a caixa.
        for (int i = lastBoxInserted.z; i < this.z; i++) {
            for (int j = lastBoxInserted.y; j < this.y; j++) {
                for (int k = lastBoxInserted.z; k < this.x; k++) {
                    //Se a caixa cabe naquela posicao, atualiza as coordenadas
                    //relativas ao container da caixa e retorna ela.
                    if (fitsHere(_box, FallBox(_box, new Vector3d(k, j, i)))) {
                        _box.relativeCoordenates = new Vector3d(k, j, i);
                        return _box;
                    } else {
                        //Se nao cabe, rotaciona ela e tenta de novo.
                        _box.rotate();
                        if (fitsHere(_box, FallBox(_box, new Vector3d(k, j, i)))) {
                            _box.relativeCoordenates = new Vector3d(k, j, i);
                            return _box;
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * Insere uma caixa dentro do container e retorna as coordenadas
     * relativas ao container de onde a caixa parou.
     */
    public boolean insertBox(Box bx) {
        for (int i = bx.relativeCoordenates.x; i < bx.relativeDimensions[0]; i++) {
            for (int j = bx.relativeCoordenates.y; j < bx.relativeDimensions[1]; j++) {
                for (int k = bx.relativeCoordenates.z; k < bx.relativeDimensions[2]; k++) {
                    this.spMatrix[i][j][k] = true;
                }

            }
        }
        return true;
    }
}