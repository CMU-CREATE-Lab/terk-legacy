package edu.cmu.ri.mrpl.TeRK.persistence;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.usertype.EnhancedUserType;
import org.hibernate.usertype.ParameterizedType;
import org.hibernate.util.ReflectHelper;

/**
 * A generic UserType that handles String-based JDK 5.0 Enums.
 *
 * @author Gavin King
 */
public class StringEnumUserType implements EnhancedUserType, ParameterizedType
   {
   private Class<Enum> enumClass;

   @SuppressWarnings({"unchecked"})
   public void setParameterValues(final Properties parameters)
      {
      final String enumClassName = parameters.getProperty("enumClassname");
      try
         {
         enumClass = ReflectHelper.classForName(enumClassName);
         }
      catch (ClassNotFoundException cnfe)
         {
         throw new HibernateException("Enum class not found", cnfe);
         }
      }

   public int[] sqlTypes()
      {
      return new int[]{Hibernate.STRING.sqlType()};
      }

   public Class returnedClass()
      {
      return enumClass;
      }

   @SuppressWarnings({"ObjectEquality"})
   public boolean equals(final Object x, final Object y)
      {
      return x == y;
      }

   public int hashCode(final Object x)
      {
      return x.hashCode();
      }

   public Object nullSafeGet(final ResultSet resultSet, final String[] names, final Object owner) throws SQLException
      {
      final String name = resultSet.getString(names[0]);
      return resultSet.wasNull() ? null : Enum.valueOf(enumClass, name);
      }

   public void nullSafeSet(final PreparedStatement preparedStatement, final Object value, final int index) throws SQLException
      {
      if (value == null)
         {
         preparedStatement.setNull(index, Hibernate.STRING.sqlType());
         }
      else
         {
         preparedStatement.setString(index, ((Enum)value).name());
         }
      }

   public Object deepCopy(final Object value)
      {
      return value;
      }

   public boolean isMutable()
      {
      return false;
      }

   public Serializable disassemble(final Object value)
      {
      return (Enum)value;
      }

   public Object assemble(final Serializable cached, final Object owner)
      {
      return cached;
      }

   public Object replace(final Object original, final Object target, final Object owner)
      {
      return original;
      }

   public String objectToSQLString(final Object value)
      {
      return '\'' + ((Enum)value).name() + '\'';
      }

   public String toXMLString(final Object value)
      {
      return ((Enum)value).name();
      }

   public Object fromXMLString(final String xmlValue)
      {
      return Enum.valueOf(enumClass, xmlValue);
      }
   }
