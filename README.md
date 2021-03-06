WeevilScout middleware is a new approach to distributed computing with Web browsers. The proliferation of Web browsers and the performance gains being achieved by current JavaScript virtual machines raises the question whether Internet browsers can become yet another middleware for distributed computing. With 2 billion users online, computing through Internet browsers has the potential to amass immense resources, thus transforming the Internet into a distributed computer ideal for common classes of distributed scientific applications such as parametric studies.

Middleware specification
========================
The platform is developed as a web applications running on a web server. As of now, it supports orchestration that can be described using XML. The unit of works it accepts are either a plain Javascript or a WebCL program.

Technology stacks
-----------------
The platform is currently developed using Scala 2.10.3, Play Framework 2.1 and Akka 2.2.3.

The Akka actor model is used extensively in the orchestration engine.

Contribution
============
Contributors are welcomed. Preferably those that have experiences working with Scala, Akka, OpenCL and/or Javascript. Please contact me at soulmagic13@gmail.com for any contribution and inquiries.

Links
=====
Published article: http://ieeexplore.ieee.org/xpl/articleDetails.jsp?arnumber=6407142
