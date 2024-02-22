# Optimal alphabet encoding
## Overview
This paper is the result of several months of work and research conducted for the purpose of completing the course Alati i metode softverskog inzenjerstva i vestacke inteligencije in the master's studies at the Faculty of Organizational Sciences, in the Software Engineering study program.


During the course, I became interested in algorithms and data structures, exploring when to use each one, as well as their performance and data structure organization. In this project, I will present a solution to a problem characteristic of the computer science domain: optimal alphabet coding. I have solved this problem using evolutionary algorithms of artificial intelligence, specifically using a genetic algorithm. This document contains a description of the problem, a brief overview of the algorithms and methodologies used, an outline of the implementation development process, challenges encountered during the work, as well as a review of the solution and a comparison with alternative approaches to solving this problem. Emphasizing the significance of algorithm complexity, I have implemented an approach to solving this problem using a "brute force" method. The solutions are implemented using the Clojure programming language, a dialect of Lisp and belonging to the class of functional programming languages. I encountered this technology during my master's studies.

## Problem description

The problem of optimal alphabet coding involves finding the most efficient way to encode a given alpabet(array of characters), tipically with the goal of minimizing the total length of the encoded message. This is particulary important in field such as data compression, where reducing the size of memory or stored data is crucial.

In this context, the problem entails determining the optimal mapping between symbols in the alphabet and their corresponding binary representations. This mapping should ideally proritize frequently occurring symbols with shorter codes, while ensuring that the codes for diiferent symbols are uniquely decodable.
*We are given an alpabet of ***N*** characters and the probabilities ***\(p_i\)*** that a randomly chosen character is character i. Let ***\(l_i\)*** is length of the encoding of character i. The objective is to minimize the expected length of message
                                          \( \sum_{i=1}^{N} \pi \cdot li \)
One of the typical ways to solve this problem is using dynamic programming and optimal tree search. Early algorithms, dating back to the 1960s, utilized optimal tree search techniques, with complexities of *\(O(n^3)\)* and *\(O(n^2)\)*. The most well-known algorithm addressing this problem is **Huffman coding**, which has a complexity of *O(nlogn)*

|Character|Frequency|Code |
|---------|---------|-----|
|    A    |    5    |  11 |
|    B    |    1    | 100 |
|    C    |    6    |  0  |
|    D    |    3    | 101 |

## License

Copyright © 2024 FIXME

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0.

This Source Code may also be made available under the following Secondary
Licenses when the conditions for such availability set forth in the Eclipse
Public License, v. 2.0 are satisfied: GNU General Public License as published by
the Free Software Foundation, either version 2 of the License, or (at your
option) any later version, with the GNU Classpath Exception which is available
at https://www.gnu.org/software/classpath/license.html.
