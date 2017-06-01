package edu.pucmm.Prueba;

import static spark.Spark.port;
import static spark.Spark.staticFiles;

import freemarker.template.Configuration;
import spark.*;
import spark.template.freemarker.FreeMarkerEngine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static spark.Spark.*;
import static spark.debug.DebugScreen.enableDebugScreen;

/**
 * Created by john on 30/05/17.
 */
public class Main {

    static final List<Estudiante> listaEstudiante = new ArrayList<>();
    static final Map<String, Object> atributos = new HashMap<>();

    public static void main(String[] args) {
        listaEstudiante.add(new Estudiante(20120467,"Manuel","Tolentino","8091234567"));


        //Seteando el puerto en Heroku
        port(getHerokuAssignedPort());

        //indicando los recursos publicos.
        staticFiles.location("/Publico");


        //Indicando la carpeta por defecto que estaremos usando.
        Configuration configuration = new Configuration(Configuration.VERSION_2_3_23);
        configuration.setClassForTemplateLoading(Main.class, "/Templates");
        FreeMarkerEngine freeMarkerEngine = new FreeMarkerEngine(configuration);


        get("/", (request, response) -> {

            return new ModelAndView(null, "index.ftl");
        }, freeMarkerEngine);

        get("/formulario", (request, response) -> {

            return new ModelAndView(null, "formulario.ftl");
        }, freeMarkerEngine);


        post("/formulario", (request, response) -> {

            listaEstudiante.add(new Estudiante(Integer.parseInt(request.queryParams("matricula")),request.queryParams("nombre"),request.queryParams("apellido"),request.queryParams("telefono")));

            atributos.put("titulo", "Formulario");

            response.redirect("/listarEstudiantes");

            return new ModelAndView(atributos, "formulario.ftl");
        }, freeMarkerEngine);


        get("/listarEstudiantes", (request, response) -> {

            Map<String, Object> attributes = new HashMap<>();

            attributes.put("titulo", "Lista de estudiantes");
            attributes.put("estudiantes",listaEstudiante);

            return new ModelAndView(attributes, "listarEstudiantes.ftl");
        }, freeMarkerEngine);


        get("/borrar/:matricula", (request, response) -> {
            System.out.println("There");
            Map<String, Object> attributes = new HashMap<>();
            Estudiante estudiante= new Estudiante(Integer.parseInt(request.params("matricula")), "Nombre", "Apellido","Telefono");

            for(Estudiante es: listaEstudiante){
                System.out.println(es.getMatricula());
                System.out.println(estudiante.getMatricula());
                if(es.getMatricula()==estudiante.getMatricula()){
                    System.out.println("Son iguales");
                    listaEstudiante.remove(es);
                    System.out.println(es);
                }
            }

            attributes.put("estudiantes",listaEstudiante);
            if(listaEstudiante.size()==0){
                System.out.println("Lista nulla");
                response.redirect("/formulario");
            }
            response.redirect("/listarEstudiantes");

            return new ModelAndView(attributes,"listarEstudiantes.ftl");
        }, freeMarkerEngine);

        get("/editar/:matricula", (request, response) -> {
           // System.out.println("There");
           // Map<String, Object> attributes = new HashMap<>();

           // Estudiante estudiante= new Estudiante(Integer.parseInt(request.params("matricula")), "Nombre", "Apellido","Telefono");

            for(Estudiante es: listaEstudiante){

                if(es.getMatricula()==Integer.parseInt(request.params("matricula"))){
                    System.out.println(Integer.parseInt(request.params("matricula")));
                    atributos.put("metodo", "editar");
                    atributos.put("titulo", "Editar estudiante.");
                    atributos.put("header", "Editar estudiante registrado.");
                    atributos.put("submit", "Actualizar");
                    atributos.put("matricula", 20131256);
                    atributos.put("nombre", "John");
                    atributos.put("apellido", "Bienaime");
                    atributos.put("telefono", "5516515");
                    System.out.println(es.getMatricula());
                }
            }
            //attributes.put("estudiante",estudiante);


            //response.redirect("/formulario");

            return new ModelAndView(atributos,"actualizar.ftl");
        }, freeMarkerEngine);


        post("/completarActualizacion", (request, response) -> {
            System.out.println("There");
            for(Estudiante es: listaEstudiante){

                if(es.getMatricula()==Integer.parseInt(request.queryParams("matricula"))){
                    System.out.println("there");
                    es.setMatricula(Integer.parseInt(request.queryParams("matricula")));
                    es.setNombre(request.queryParams("nombre"));
                    es.setApellido(request.queryParams("apellido"));
                    es.setTelefono(request.queryParams("telefono"));
                    System.out.println("there2");
                }
            }

            response.redirect("/listarEstudiantes");

            return "";
        });

    }







    // Obtener el estudiante enviado por el request
    private static Estudiante parseEstudiante(Request req) {
        Estudiante estudiante = new Estudiante();
        estudiante.setNombre(req.queryParams("nombre"));
        estudiante.setApellido(req.queryParams("apellido"));
        estudiante.setMatricula(Integer.parseInt(req.queryParams("matricula")));
        estudiante.setTelefono(req.queryParams("telefono"));

        return estudiante;
    }


    /**
     * Metodo para setear el puerto en Heroku
     * @return
     */
    private static int getHerokuAssignedPort() {
        ProcessBuilder processBuilder = new ProcessBuilder();
        if (processBuilder.environment().get("PORT") != null) {
            return Integer.parseInt(processBuilder.environment().get("PORT"));
        }
        return 4567; //return default port if heroku-port isn't set (i.e. on localhost)
    }
}
