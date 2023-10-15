package br.com.victor3r.todolist.utils;

import java.util.HashSet;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapperImpl;

public class Utils {
  public static void copyNonNullProperties(Object source, Object target) {
    BeanUtils.copyProperties(source, target, getNullPropertyNames(source));
  }

  public static String[] getNullPropertyNames(Object source) {
    final var beanWrapper = new BeanWrapperImpl(source);

    var propertyDescriptors = beanWrapper.getPropertyDescriptors();

    var emptyNames = new HashSet<String>();

    for (var pd : propertyDescriptors) {
      var srcValue = beanWrapper.getPropertyValue(pd.getName());

      if (srcValue == null) {
        emptyNames.add(pd.getName());
      }
    }

    var result = new String[emptyNames.size()];

    return emptyNames.toArray(result);
  }
}
