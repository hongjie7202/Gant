//  Gant -- A Groovy build framework based on scripting Ant tasks.
//
//  Copyright © 2008 Russel Winder
//
//  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in
//  compliance with the License. You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
//  Unless required by applicable law or agreed to in writing, software distributed under the License is
//  distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
//  implied. See the License for the specific language governing permissions and limitations under the
//  License.

package org.codehaus.gant.tests

/**
 *  A test for accessing the properties in the AntBuilder object.
 *
 *  @author Russel Winder <russel.winder@concertant.com>
 */
final class Environment_Test extends GantTestCase {
  final java5orHigher = System.properties.'java.version'.split ( /\./ )[1] > '4'
   //  Don't use an instance initializer since they do not get compiled properly on first compile only on a
   //  second separate compile. :-(
  final String groovyHome ;
  public Environment_Test ( ) {
    if ( System.properties.'groovy.home' != null ) { groovyHome = System.properties.'groovy.home' }
    else if ( java5orHigher ) { groovyHome = System.getenv ( ).'GROOVY_HOME' }
    else {
      def ant = new AntBuilder ( )
      ant.property ( environment : 'environment' )
      groovyHome = ant.project.properties.'environment.GROOVY_HOME'
    }
  } 
  void testConsistencyJava5OrHigher ( ) {


    System.err.println ( groovyHome )
    System.err.println ( System.properties.'groovy.home' )
    System.err.println ( System.getenv ( ).'GROOVY_HOME' )

    if ( java5orHigher ) { assertEquals ( groovyHome , System.getenv ( ).'GROOVY_HOME' ) }
  }
  void testAntPropertiesSet ( ) {
    //
    //  NB System.properties.'groovy.home' is only set if Groovy/Gant is initiated via the standard Groovy
    //  startup.  The property is set in the shell scripts / native launcher.
    //
    System.setIn ( new StringBufferInputStream ( '''
target ( report : '' ) {
  if ( System.properties.'groovy.home' != null ) {
    println ( System.properties.'groovy.home'.equals ( Ant.project.properties.'environment.GROOVY_HOME' ) ? 'true' : 'false' )
  }
  else {
    println ( Ant.project.properties.'environment.GROOVY_HOME' != null ? 'true' : 'false' )
  }
}
''' ) )
    assertEquals ( 0 , gant.process ( [ '-f' , '-' , 'report' ] as String[] ) )
    assertEquals ( 'true\n' , output )
  }
  void testAntEnvironmentGroovyHome ( ) {
    System.setIn ( new StringBufferInputStream ( '''
target ( report : '' ) { print ( Ant.project.properties.'environment.GROOVY_HOME' ) }
''' ) )
    assertEquals ( 0 , gant.process ( [ '-f' , '-' , 'report' ] as String[] ) )
    assertEquals ( groovyHome , output )
  }
  void testSystemEnvironmentGroovyHome ( ) {
    if ( java5orHigher ) {
      System.setIn ( new StringBufferInputStream ( '''
target ( report : '' ) { print ( System.getenv ( ).'GROOVY_HOME' ) }
''' ) )
      assertEquals ( 0 , gant.process ( [ '-f' , '-' , 'report' ] as String[] ) )
      assertEquals ( groovyHome , output )
    }
  }
  void testSystemPropertiesGroovyHome ( ) {
    System.setIn ( new StringBufferInputStream ( '''
target ( report : '' ) { print ( System.properties.'groovy.home' ) }
''' ) )
    assertEquals ( 0 , gant.process ( [ '-f' , '-' , 'report' ] as String[] ) )
    assertEquals (  ( System.properties.'groovy.home' == null ) ? 'null' : groovyHome , output )
  }
}