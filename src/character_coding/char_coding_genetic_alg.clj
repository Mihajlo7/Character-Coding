(ns character-coding.char-coding-genetic-alg)

;; Genetic algorithm

(def letters [['A' 5]
              ['B' 1]
              ['C' 6]
              ['D' 3]
              ['E' 2]
              ['F' 1]
              ['G' 4]
              ['H' 4]
              ['I' 2]
              ['J' 2]])

;; First we should create initial population for our problem


;;
;; After much consideration about the best way to encode letters into binary codes,
;; I have come to the following conclusion. The initial idea was to assign a unique decimal value to each letter;
;; for example, if there are 26 letters, each letter would be assigned a value from 1 to 26.
;; A better solution would be to use values from 0 to 25 without repetition.
;;
;; After considering this further, I've come to the conclusion that this isn't the best solution.
;; For instance, a single character can be represented in binary as 0, but it can also be represented as 00 or 000,
;; all of which have the same decimal value. This approach leads to an optimal solution because, for the fitness function,
;; we are seeking a solution that minimizes the size of the memory occupied by characters.
;; The focus is on the number of binary characters rather than their values.
;;
;; Even though this solution is superior in terms of the objective function,
;; it is more challenging to implement due to later steps in the genetic algorithm.

(defn get-max-number-of-bytes
  "Calculate max number of bytes to represent numbers 1..char-count"
  [char-count]
  (int (Math/ceil (Math/sqrt char-count)))
  )

;; Because of latter part of genetic algorithm, we should convert array of characters and frequences to map
(defn array-to-map
  "Convert array to map"
  [array]
  (into {} array)
  )

(defn create-initial-population
  "Function generates initial population
   Input: size of population, length of chromosome
   Output: Randomly generated population"
  [population-size,chromosome-length])


