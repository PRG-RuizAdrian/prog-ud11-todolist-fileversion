package es.progcipfpbatoi.modelo.dao;

import es.progcipfpbatoi.exceptions.DatabaseErrorException;
import es.progcipfpbatoi.exceptions.NotFoundException;
import es.progcipfpbatoi.modelo.dto.Categoria;
import es.progcipfpbatoi.modelo.dto.Tarea;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class FileTareaDAO implements TareaDAO{

    private static final String DATABASE_FILE = "resources/database/tareas.txt";
    private static final int ID = 0;
    private static final int DESCRIPCION = 1;
    private static final int FECHA = 2;
    private static final int FINALIZADO = 3;
    private static final int CATEGORIA = 4;

    private File file;

    public FileTareaDAO() {
        this.file = new File(DATABASE_FILE);
    }

    @Override
    public ArrayList<Tarea> findAll() {
        ArrayList<Tarea> tareas= new ArrayList<>();
        try{
           BufferedReader bufferedReader = new BufferedReader(new FileReader(DATABASE_FILE));
            
            do {
                String register = bufferedReader.readLine();
                if(register == null){
                    return tareas;
                }

                String[] componente = register.split(";");

                int id = Integer.parseInt(componente[ID]);
                String descripcion = componente[DESCRIPCION];
                LocalDateTime fecha = LocalDateTime.parse(componente[FECHA], DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                boolean finalizado = Boolean.parseBoolean(componente[FINALIZADO]);
                Categoria categoria = Categoria.valueOf(componente[CATEGORIA]);

                tareas.add(new Tarea(id,descripcion,fecha,finalizado,categoria));

                System.out.println(register);
            }while (true);

        }catch (IOException e){
            System.out.println("Se ha producido durante la lectura del archivo" + e.getMessage());
        }
        return tareas;
    }

    @Override
    public ArrayList<Tarea> findAll(String text) throws DatabaseErrorException {
        ArrayList<Tarea> tareas = new ArrayList<>();
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(DATABASE_FILE));
            
            do{
                String register = bufferedReader.readLine();
                if (register == null) {
                    return tareas;
                }

                String[] componente = register.split(";");
                int id = Integer.parseInt(componente[ID]);
                String descripcion = componente[DESCRIPCION];
                LocalDateTime fecha = LocalDateTime.parse(componente[FECHA], DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                boolean finalizado = Boolean.parseBoolean(componente[FINALIZADO]);
                Categoria categoria = Categoria.valueOf(componente[CATEGORIA]);

                Tarea tarea = new Tarea(id,descripcion,fecha,finalizado,categoria);
                if (descripcion.startsWith(text)) {
                    tareas.add(tarea);
                }

            }while (true);
        } catch (IOException e) {
            throw new DatabaseErrorException("No se encuentra el archivo");
        }
    }

    @Override
    public Tarea getById(int id) throws NotFoundException, DatabaseErrorException {
        try (FileReader fileReader = new FileReader(this.file);
             BufferedReader bufferedReader = new BufferedReader(fileReader)) {

            do {
                String register = bufferedReader.readLine();
                if (register == null) {
                    throw new NotFoundException("Tarea no encontrada");
                } else if (!register.isBlank()) {
                    String[] fields = register.split(";");
                    int codigo = Integer.parseInt(fields[ID]);
                    String descripcion = fields[DESCRIPCION];
                    LocalDateTime fecha = LocalDateTime.parse(fields[FECHA], DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                    boolean finalizado = Boolean.parseBoolean(fields[FINALIZADO]);
                    Categoria categoria = Categoria.parse(fields[CATEGORIA]);
                    Tarea tarea = new Tarea(codigo, descripcion, fecha, finalizado, categoria);
                    if (tarea.getId() == id) {
                        return tarea;
                    }
                }
            } while (true);
        } catch (IOException e) {
            e.printStackTrace();
            throw new DatabaseErrorException("Ocurrió un error en el acceso a la base de datos");
        }
    }

    @Override
    public boolean save(Tarea tarea) throws IOException {
        ArrayList<Tarea> tareas = findAll();
        boolean tareaActualizada = false;
        for (int i = 0; i < tareas.size(); i++) {
            Tarea tareaI = tareas.get(i);
            if (tareaI.getDescripcion().equals(tarea.getDescripcion())) {
                tareas.set(i, tarea);
                tareaActualizada = true;
                System.out.println("Tarea actualizada");

            }
        }
        if (!tareaActualizada) {
            tareas.add(tarea);
            System.out.println("Tarea añadida");
        }

        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(this.file))) {
            for (Tarea tareaI : tareas) {
                
                int id = tareaI.getId();
                String idString= String.valueOf(id);
                LocalDateTime fecha = tareaI.getFechaAlta();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                String fechaString = fecha.format(formatter);
                boolean finalizado = tareaI.isFinalizada();
                String finalizadoString= String.valueOf(finalizado);
                Categoria categoria = tareaI.getCategoria();
                String categoriaString = categoria.toString();

                String lineaTarea = idString+";"+tareaI.getDescripcion()+";"+fechaString+";"+finalizadoString+";"+categoriaString;
                bufferedWriter.write(lineaTarea);
                bufferedWriter.newLine();
            }
        } catch (IOException e) {
            throw new IOException("Ocurrió un error en el acceso a la base de datos");
        }
        return true;
    }

}
