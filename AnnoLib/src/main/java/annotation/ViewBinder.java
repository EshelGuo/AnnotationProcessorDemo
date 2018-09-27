package annotation;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class ViewBinder {

    public static void bind(Object activity){
        if(activity == null)
            return;
        try {
            Class<?> clazz = Class.forName("android.app.Activity");
            if(clazz != null){
                if(clazz.isInstance(activity)){
                    Class<?> activityClass = activity.getClass();
                    Class<?> ViewBindingClass = Class.forName(activityClass.getCanonicalName() + "_ViewBinding");
                    Constructor<?> constructor = ViewBindingClass.getConstructor(activityClass);
                    Object ViewBinding = constructor.newInstance(activity);
                }
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
