Shame on me for copying Jsoup lib sources to my project. I was trying to extend functionality of HttpConnection class
using wrapper technique, dynamicProxy technique and even Spring AOP methods. But I've failed. The problems are:
  1. Connection has private constructor.
  2. Connection is very linked inside the lib.
  3. Connection is using kind of a builder pattern, which replaces my "extended" object with it's native HttpConnection.

My target is to extend HttpConnection's execute() method which will be auto inlining gathered cookies and correct referrer.
I did it with copying sources of Jsoup lib to my project.
I had to modify Entities.java file which was loading .properties as resources (around line 241).

I'am very curious if there was much more elegant way to extend the class.