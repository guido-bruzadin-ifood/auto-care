package br.com.tribotech.autocare.util;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class PackageUtil {
    /**
     * Scans all classes accessible from the context class loader which belong to the given package and subpackages.
     *
     * @param packageName The base package
     * @return The classes
     * @throws ClassNotFoundException
     * @throws IOException
     */
    @SuppressWarnings("rawtypes")
	public static Class[] getClasses(String packageName) throws ClassNotFoundException, IOException {
    	ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        assert classLoader != null;
        
        String path = packageName.replace('.', '/');
        
        System.out.println(" PACKAGE COLUMN_MANUFACTURER  "+path);
        
        Enumeration<URL> resources = classLoader.getResources(path);
        List<File> dirs = new ArrayList<File>();
        
                
        while (resources.hasMoreElements()) {
        	System.out.println(" 11111111111111111111111111111111 ");
        	
            URL resource = resources.nextElement();
            System.out.println(resource.getPath());
        	
            
            
            dirs.add(new File(resource.getFile()));
        }
        
        
        ArrayList<Class> classes = new ArrayList<Class>();
        for (File directory : dirs) {
            classes.addAll(findClasses(directory, packageName));
        }
        return classes.toArray(new Class[classes.size()]);
    }

    /**
     * Recursive method used to find all classes in a given directory and subdirs.
     *
     * @param directory   The base directory
     * @param packageName The package name for classes found inside the base directory
     * @return The classes
     * @throws ClassNotFoundException
     */
    @SuppressWarnings("rawtypes")
	private static List<Class> findClasses(File directory, String packageName) throws ClassNotFoundException {
        List<Class> classes = new ArrayList<Class>();
        
        System.out.println(" Pacote: "+packageName);
        System.out.println(" Dir: "+directory.getName());
        
        if (!directory.exists()) {
        	System.out.println("O diretório não foi encontrado !");
            return classes;
        }
        File[] files = directory.listFiles();
        for (File file : files) {
        	System.out.println(file.getName());
            if (file.isDirectory()) {
                assert !file.getName().contains(".");
                classes.addAll(findClasses(file, packageName + "." + file.getName()));
            } else if (file.getName().endsWith(".class")) {
                classes.add(Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6)));
            }
        }
        return classes;
    }

}
