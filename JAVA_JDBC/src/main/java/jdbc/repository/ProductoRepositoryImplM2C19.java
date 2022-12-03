package jdbc.repository;

import jdbc.models.CategoriaDto;
import jdbc.models.ProductoDto;
import jdbc.util.ConexionBD_M2C12;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * CLase creada para replicar y adquirir los conocimientos
 * santalbu
 * 20221007
 *
 * Clase 19 se agrega la relacion productos con categoria *
 *
 *
 * Se crea la clase productoRepository generica para poder accerder a la
 * interfaz atraves del objeto generico*/


public class ProductoRepositoryImplM2C19 implements Repository<ProductoDto> {

    private Connection conn;
    private Statement stmt;
    private ResultSet result;

    public ProductoRepositoryImplM2C19(){
        try {
            getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Connection getConnection() throws SQLException {
        return conn = ConexionBD_M2C12.getInstance();
    }

    @Override
    public List<ProductoDto> listar() {

        List<ProductoDto> listProductos = new ArrayList<>();
        try {
            getConnection();
            stmt = conn.createStatement();
            stmt.executeQuery("select * from productos p " +
                    "  join categorias c " +
                    "  on p.id_categoria_fk = c.idcategorias");
            result = stmt.getResultSet();

            while(result.next()){
                ProductoDto product = new ProductoDto();
                CategoriaDto categoria = new CategoriaDto();
                product.setId(result.getLong(1));
                product.setNombre(result.getString(2));
                product.setPrecio(result.getDouble(3));
                product.setFecha(result.getString(4));
                categoria.setIdCategoria(result.getLong(6));
                categoria.setNombreCategoria(result.getString(7));
                product.setIdCategoriaFk(categoria);
                listProductos.add(product);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            cerrarConexciones( conn,result,stmt);
        }

        return listProductos;
    }

    @Override
    public ProductoDto findForId(Long id) {
        try {
            getConnection();
            Integer contador = 1;
            PreparedStatement prepareStmt = conn.prepareStatement("select * from productos p " +
                    "                     join categorias c" +
                    "                     on p.id_categoria_fk = c.idcategorias WHERE IDPRODUCTOS = ?");
            prepareStmt.setLong(contador++,id);
            prepareStmt.executeQuery();
            result = prepareStmt.getResultSet();

            if(result.next()){
                ProductoDto product = crearProducto();
                return product;
            }

        }catch (SQLException e) {
            e.printStackTrace();
        } finally {
            cerrarConexciones( conn,result,stmt);
        }
        return null;
    }

    private ProductoDto crearProducto() throws SQLException {
        ProductoDto product = new ProductoDto();
        CategoriaDto categoria = new CategoriaDto();
        product.setId(result.getLong(1));
        product.setNombre(result.getString(2));
        product.setPrecio(result.getDouble(3));
        product.setFecha(result.getString(4));
        categoria.setIdCategoria(result.getLong(6));
        categoria.setNombreCategoria(result.getString(7));
        product.setIdCategoriaFk(categoria);
        return product;
    }

    @Override
    public Integer save(ProductoDto productoDto) {
        try {
            getConnection();
            PreparedStatement prepareStmt = null;
            if(productoDto.getId() == null){
                prepareStmt = conn.prepareStatement("INSERT INTO productos" +
                        "(nombre_producto,precio,fecha_registro,id_categoria_fk)" +
                        " VALUES" +
                        "(?,?,?,?)");
            }else{
                prepareStmt = conn.prepareStatement("UPDATE productos " +
                        "SET " +
                        "nombre_producto = ?, " +
                        "precio = ?, " +
                        "fecha_registro = ?," +
                        "id_categoria_fk = ? " +
                        "WHERE idproductos = ?");
            }


            Integer contador = 1;
            prepareStmt.setString(contador++,productoDto.getNombre());
            prepareStmt.setDouble(contador++,productoDto.getPrecio());
            prepareStmt.setString(contador++,productoDto.getFecha());
            prepareStmt.setLong(contador++,productoDto.getIdCategoriaFk().getIdCategoria());
            if(productoDto.getId() != null){
                prepareStmt.setLong(contador++,productoDto.getId());
            }
            return prepareStmt.executeUpdate();
            // setDate segundo paramatro es de tipo java.sql.date este extiende de
            // java.util.date, como el parametro es de java.util.date toca convertirlo en java.sql.Date

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            cerrarConexciones( conn,result,stmt);
        }

    return 0;

    }

    @Override
    public Integer delete(ProductoDto productoDto) {

        try {
            getConnection();
            PreparedStatement prepareStmt = null;
             prepareStmt = conn.prepareStatement("DELETE from productos " +
                        "WHERE idproductos = ?");

            Integer contador = 1;
            prepareStmt.setLong(contador++,productoDto.getId());
            return prepareStmt.executeUpdate();


        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            cerrarConexciones( conn,result,stmt);
        }

        return 0;
    }

    public static void cerrarConexciones(Connection con, ResultSet r, Statement s){
        try {

            if(r != null){
                r.close();
            }

            if(s != null){
                s.close();
            }

            if(con != null){
                con.close();
            }
        } catch (SQLException e) {
           e.printStackTrace();
        }

    }
}
