# The below script should 
# print two numbers and their sum
# and exit without error

# First start JVM by compiling
# javac -cp $YOUR_CLASSPATH AdditionApplication.java
# and then running
# java -cp $YOUR_CLASSPATH:. AdditionApplication
# followed by
# python run_py4j_test.py

# Borrowed from
# https://stackoverflow.com/questions/22386399/simplest-example-with-py4j

from py4j.java_gateway import JavaGateway
gateway = JavaGateway() 
random = gateway.jvm.java.util.Random()
number1 = random.nextInt(10)
number2 = random.nextInt(10)
print(number1,number2)
addition_app = gateway.entry_point
addition1 = addition_app.addition(number1,number2)
print(addition1)
print('Test successfully completed, exiting')

