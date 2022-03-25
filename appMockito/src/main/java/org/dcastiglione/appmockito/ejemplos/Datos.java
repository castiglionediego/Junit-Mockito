package org.dcastiglione.appmockito.ejemplos;

import org.dcastiglione.appmockito.ejemplos.models.Examen;

import java.util.Arrays;
import java.util.List;

public class Datos {

    public static final List<Examen> EXAMEN_LIST = Arrays.asList(new Examen(5L, "Matematicas"), new Examen(6L, "Lenguaje")
            , new Examen(7L, "Historia"));

    public static final List<String> PREGUNTAS = Arrays.asList("aritmetica", "integrales", "derivadas", "trigonometria", "geomentria");


    public static final Examen EXAMEN = new Examen(8L, "Fisica");

}
