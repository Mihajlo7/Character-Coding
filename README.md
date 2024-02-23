# Optimal alphabet encoding
## Overview
This paper is the result of several months of work and research conducted for the purpose of completing the course Alati i metode softverskog inzenjerstva i vestacke inteligencije in the master's studies at the Faculty of Organizational Sciences, in the Software Engineering study program.


During the course, I became interested in algorithms and data structures, exploring when to use each one, as well as their performance and data structure organization. In this project, I will present a solution to a problem characteristic of the computer science domain: optimal alphabet coding. I have solved this problem using evolutionary algorithms of artificial intelligence, specifically using a genetic algorithm. This document contains a description of the problem, a brief overview of the algorithms and methodologies used, an outline of the implementation development process, challenges encountered during the work, as well as a review of the solution and a comparison with alternative approaches to solving this problem. Emphasizing the significance of algorithm complexity, I have implemented an approach to solving this problem using a "brute force" method. The solutions are implemented using the Clojure programming language, a dialect of Lisp and belonging to the class of functional programming languages. I encountered this technology during my master's studies.

## Problem description

The problem of optimal alphabet coding involves finding the most efficient way to encode a given alpabet(array of characters), tipically with the goal of minimizing the total length of the encoded message. This is particulary important in field such as data compression, where reducing the size of memory or stored data is crucial.

In this context, the problem entails determining the optimal mapping between symbols in the alphabet and their corresponding binary representations. This mapping should ideally proritize frequently occurring symbols with shorter codes, while ensuring that the codes for diiferent symbols are uniquely decodable.
*We are given an alpabet of ***N*** characters and the probabilities **$`p_i`$** that a randomly chosen character is character i. Let **$`l_i`$** is length of the encoding of character i. The objective is to minimize the expected length of message
```math
 \sum_{i=1}^{N} (p_i \cdot l_i) 
```
One of the typical ways to solve this problem is using dynamic programming and optimal tree search. Early algorithms, dating back to the 1960s, utilized optimal tree search techniques, with complexities of *O($`n^3`$)* and *O($`n^2`$)*. The most well-known algorithm addressing this problem is **Huffman coding**, which has a complexity of *O(nlogn)*

|Character|Frequency|Code |
|---------|---------|-----|
|    A    |    5    |  11 |
|    B    |    1    | 100 |
|    C    |    6    |  0  |
|    D    |    3    | 101 |

## Evolutionary algorithm

Evolutionary algorithms are inspired by the process of natural selection and evolution observed in biological systems.

Evolution is an unusual and chaotic system that generates variations in life forms, some of which are better adapted to specific environments. According to the theory of evolution, a population exhibits the following characteristics: 
- *diversity* – individuals within the population have different genetic traits 
- *heredity* – offspring inherit genetic characteristics from their parents
- *selection* – a mechanism that measures the adaptability of an individual, with stronger individuals having a higher probability of survival; 
- *reproduction* – two individuals in the population reproduce to create offspring
- *crossover and offspring* – offspring produced through reproduction contain a combination of genes from their parents and undergo minor random changes in the genetic code

Knowledge from biological evolution has been leveraged to find optimal solutions to practical problems by generating various solutions and approaching those with better performance over many generations.

Evolutionary algorithms are powerful in solving optimization problems where the solution consists of a large number of permutations or choices. For these problems, there are usually many valid solutions, only some of which are optimal.

## Genetic algorithm

Genetic algorithm is specific algorithm from family of evolutionary algorithms. 
The genetic algorithm is utilized to explore vast search spaces in pursuit of good solutions. It does not guarantee finding the best solution but rather attempts to find the best global solution while avoiding local solutions. The life cycle of a genetic algorithm will be illustrated through an example of solving the optimal alphabet coding problem.
### Creating population
We will take, for example, the following sequence as the input parameter into the algorithm
<p align="center"><font size="8">ABBCAADBABBEE</font></p>

Now, a table is created containing all distinct characters along with their occurrences in the sequence or text.
  
|Character|Frequency|
|---------|---------|
|    A    |    4    |
|    B    |    5    |
|    C    |    1    |
|    D    |    1    |
|    E    |    2    |

In a genetic algorithm, it is crucial to properly encode the solution space, which requires careful design of possible states. A ***state*** is a data structure with specific rules and can contain a solution to the problem. In line with genetic algorithms and terminology in evolutionary theory, a state is called an ***individual***. A ***population*** consists of multiple individuals. Each individual contains a set of characteristics, referred to as a ***chromosome***. The chromosome contains multiple ***genes***, with one gene describing one characteristic of the individual. In this case, one gene represents the way in which a specific letter will be encoded, i.e., the number of bits that will comprise that code. Here's an example of a chromosome (individual)



| 1 | 3 | 2 | 1 | 4 | 
|---|---|---|---|---|

| 2 | 2 | 2 | 4 | 4 |
|---|---|---|---|---|

> [!NOTE]
> Each chromosome is of length L, where L equals the number of distinct characters. Each gene represents a random number from 1 to N, where N represents the number of bits needed to describe each gene. For example, if we have 5 distinct letters, each letter can be represented by combinations of a binary code with 3 digits, for instance: 000, 001, 010, etc. This way, it's possible to find a solution that requires less memory.

```clojure
(create-initial-population-optimised population-size chromosome-length)
```

### Defining the fitness function


The ***fitness function*** represents a measure of how good a solution is. In this example, the fitness function is obtained by summing the products of the character frequencies and the number of bits used for encoding.
```math
 f(x)=\sum_{i=1}^{L} (p_i \cdot c_i)
```
where *L* is the number of characters, **$`p_i`$** is the frequency of character i and **$`c_i`$** is the number of bits used to encode character i.


However, there is a constraint to consider when calculating the fitness function. During the computation of the fitness function, we need to consider the number of distinct genes. For example, if a gene has a value of 1, it means that the character is encoded with a single binary digit, 0 or 1. Due to this characteristic of binary encoding, one chromosome cannot have more than 2 genes with a value of 1. From this property of binary encoding, we can derive a constraint for the fitness function
```math
\text{count}(g) \leq 2^{g} 
```
where **g** is value og single gene.
If the constraint is not met, the chromosome is considered invalid, and the individual is discarded from the population
```clojure
(survival population letters)
```
