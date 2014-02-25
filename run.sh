#!/bin/bash

#export SCALA_OPTS="-J-Xdebug -J-Xrunjdwp:transport=dt_socket,address=8787,server=y,suspend=y"

export SCALA_HOME="${HOME}/Products/tools/scala-2.10.3/"
export INFINISPAN_HOME="${INFINISPAN_HOME:-../infinispan-6.0.0.Final-all/}"


for jar in ${INFINISPAN_HOME}/lib/*.jar
do
  if [ $(echo "${jar}" | grep -e 'scala' -c ) -eq 0 ]; then
    CLASSPATH=${CLASSPATH}:${jar}
  fi
done
export CLASSPATH=.:${CLASSPATH}
javac -cp ${CLASSPATH} MyProduct.java
${SCALA_HOME}/bin/scala ${SCALA_OPTS} -usejavacp "$@"
rm -rf MyProduct.class org.infinispan.query.remote.indexing.ProtobufValueWrapper
