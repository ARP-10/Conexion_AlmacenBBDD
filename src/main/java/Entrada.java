import JSON.Lectura;
import database.GestionDB;

import java.sql.*;
import java.util.Scanner;

import database.SchemeDB;
import org.json.JSONObject;
import org.json.JSONArray;


public class Entrada {
    public static void main(String[] args) {
        int opcion = 0;
        boolean menu = false;
        Scanner sc = new Scanner(System.in);

        // Conexion BBDD
        Connection connection = GestionDB.getConnection();
        if (connection == null) {
            System.out.println("ERROR! La conexión ha fallado");
        } else {
            System.out.println("Conexión establecida");
            menu = true;

            //insertarProductos(connection); -> Comentado para que no se inserte de nuevo


            while (menu) {
                System.out.println("Escoge entre las siguientes opciones: ");
                System.out.println("1. Insertar nuevo empleado");
                System.out.println("2. Crear nuevo pedido");
                System.out.println("3. Mostrar todos los empleados");
                System.out.println("4. Mostrar todos los productos");
                System.out.println("5. Mostrar todos los pedidos");
                System.out.println("6. Salir");

                opcion = sc.nextInt();

                switch (opcion) {
                    case 1:
                        insertarEmpleado(connection);
                        break;
                    case 2:
                        crearPedido(connection);
                        break;
                    case 3:
                        mostrarEmpleados(connection);
                        break;
                    case 4:
                        mostrarProductos(connection);
                        break;
                    case 5:
                        mostrarPedidos(connection);
                        break;
                    case 6:
                        System.out.println("Saliendo del programa.....");
                        menu = false;
                        break;
                }
            }


        }

    }


    // Introduce todos los productos del JSON
    public static void insertarProductos(Connection connection){

        Lectura lectura = new Lectura("https://dummyjson.com/products");
        JSONObject response = lectura.convertirJSON(lectura.leerArchivo());
        JSONArray productos = response.getJSONArray("products");

        //Obtener productos del JSON
        for(int i=0;i<productos.length();i++) {
            JSONObject producto = productos.getJSONObject(i);
            String nombre = producto.getString("title");
            String descripcion = producto.getString("description");
            int precio = producto.getInt("price");
            int cantidad = producto.getInt("stock");

            //Añadir a nuestra BBDD
            try {
                PreparedStatement preparedStatement = connection.prepareStatement(String.format("INSERT INTO %s (%s,%s,%s,%s) VALUE (?,?,?,?)",
                        SchemeDB.TAB_NAME_PRODUCTOS,
                        SchemeDB.TAB_PRODUCTOS_COL[0],
                        SchemeDB.TAB_PRODUCTOS_COL[1],
                        SchemeDB.TAB_PRODUCTOS_COL[2],
                        SchemeDB.TAB_PRODUCTOS_COL[3]));
                preparedStatement.setString(1, nombre);
                preparedStatement.setString(2, descripcion);
                preparedStatement.setInt(3, cantidad);
                preparedStatement.setInt(4, precio);
                preparedStatement.execute();
                System.out.println("Los productos han sido introducidos");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }

    // Crear empleado nuevo
    private static void insertarEmpleado(Connection connection) {
        Scanner sc = new Scanner(System.in);
        System.out.println("Nombre: ");
        String nombreEmpleado = sc.nextLine();
        System.out.println("Apellidos: ");
        String apellidosEmpleado = sc.nextLine();
        System.out.println("Correo: ");
        String correoEmpleado = sc.nextLine();

        //Añadir a nuestra BBDD
        try {
            Statement statement = connection.createStatement();
            int rows = statement.executeUpdate(String.format("INSERT INTO %s (%s,%s,%s) VALUES ('%s','%s','%s')",
                    SchemeDB.TAB_NAME_EMPLEADOS,
                    SchemeDB.TAB_EMPLEADOS_COL[0],
                    SchemeDB.TAB_EMPLEADOS_COL[1],
                    SchemeDB.TAB_EMPLEADOS_COL[2],
                    nombreEmpleado,apellidosEmpleado,correoEmpleado));
            statement.close();
            System.out.println("El empleado ha sido introducido.");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // Crear un nuevo pedido
    private static void crearPedido(Connection connecion) {
        try {
            Scanner sc = new Scanner(System.in);
            System.out.println("Inserta el id del producto sobre el cual quieres realizar un pedido: (1-12)");
            int id = sc.nextInt();
            System.out.println("Indica la cantidad que quieres pedir de dicho producto: ");
            int cantidadProductos = sc.nextInt();

            Statement statement = connecion.createStatement();

            ResultSet rs = statement.executeQuery(String.format("SELECT (%s*%d) as precioTotal FROM %s WHERE id = %d",
                    SchemeDB.TAB_PRODUCTOS_COL[3],
                    cantidadProductos,
                    SchemeDB.TAB_NAME_PRODUCTOS,
                    id
            ));
            rs.next();

            int precioTotal = rs.getInt("precioTotal");
            String descripcion = "Id de producto: " + id + "\nCantidad: " + cantidadProductos;

            //Añadir a nuestra BBDD
            statement.executeUpdate(String.format("INSERT INTO %s (%s,%s,%s) VALUE ('%s',%d,%d)",
                    SchemeDB.TAB_NAME_PEDIDOS,
                    SchemeDB.TAB_PEDIDOS_COL[0],
                    SchemeDB.TAB_PEDIDOS_COL[1],
                    SchemeDB.TAB_PEDIDOS_COL[2],
                    descripcion,precioTotal,id));
            System.out.println("El pedido ha sido introducido correctamente");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Mostrar empleados
    private static void mostrarEmpleados(Connection connection) {
        try {
            StringBuilder empleados = new StringBuilder();
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("SELECT * FROM " + SchemeDB.TAB_NAME_EMPLEADOS);
            while(rs.next()){
                int id = rs.getInt(SchemeDB.TAB_ID);
                String nombreEmpleado = rs.getString(SchemeDB.TAB_EMPLEADOS_COL[0]);
                String apellidosEmpleado = rs.getString(SchemeDB.TAB_EMPLEADOS_COL[1]);
                String correoEmpleado = rs.getString(SchemeDB.TAB_EMPLEADOS_COL[2]);
                empleados.append(String.format("Id: %d | Nombre: %s | Apellidos: %s| Correo: %s\n",
                        id,
                        nombreEmpleado,
                        apellidosEmpleado,
                        correoEmpleado
                ));
            }
            System.out.println(empleados);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // Mostrar productos
    public static void mostrarProductos(Connection connection){
        try {
            StringBuilder productos = new StringBuilder();
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("SELECT * FROM " + SchemeDB.TAB_NAME_PRODUCTOS);
            while(rs.next()){
                int id = rs.getInt(SchemeDB.TAB_ID);
                String nombre = rs.getString(SchemeDB.TAB_PRODUCTOS_COL[0]);
                String descripcion = rs.getString(SchemeDB.TAB_PRODUCTOS_COL[1]);
                int cantidad = rs.getInt(SchemeDB.TAB_PRODUCTOS_COL[2]);
                int precio = rs.getInt(SchemeDB.TAB_PRODUCTOS_COL[3]);
                productos.append(String.format("Id: %d | Nombre: %s | Descripcion: %s| Cantidad: %d | Precio: %d \n", id,
                        nombre,
                        descripcion,
                        cantidad,
                        precio
                ));
            }
            System.out.println(productos);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    // Mostrar pedidos
    public static void mostrarPedidos(Connection connection){
        try {
            StringBuilder pedidos = new StringBuilder();
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("SELECT * FROM " + SchemeDB.TAB_NAME_PEDIDOS);
            while(rs.next()){
                int id = rs.getInt(SchemeDB.TAB_ID);
                String descripcion = rs.getString(SchemeDB.TAB_PEDIDOS_COL[0]);
                int precioTotal = rs.getInt(SchemeDB.TAB_PEDIDOS_COL[1]);
                int idProducto = rs.getInt(SchemeDB.TAB_PEDIDOS_COL[2]);
                pedidos.append(String.format("Id: %d | Descripcion: %s | Precio Total: %d | Id Producto: %d\n",
                        id,
                        descripcion,
                        precioTotal,
                        idProducto
                ));
            }
            System.out.println(pedidos);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

}