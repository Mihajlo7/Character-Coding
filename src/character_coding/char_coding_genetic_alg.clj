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

; Initialization of population
(defn generate-chromosome
  [chromosome-type]
  (let [max-number-of-bytes (get-max-number-of-bytes (count chromosome-type))]
    (mapv (fn [gene] (conj gene (inc (rand-int max-number-of-bytes))))  chromosome-type)
  ))
(defn create-initial-population
  "Function generates initial population
   Input: size of population, structure of chromosome
   Output: Randomly generated population"
  [population-size,chromosome-type]
  (let [population (transient [])]
    (dotimes [_ population-size]
      (let [current-individual (generate-chromosome chromosome-type)]
        (conj! population current-individual)))
    (persistent! population)))

(defn generate-chromosome-1
  [chromosome-size max-number-bytes]
  (vec (repeatedly chromosome-size #(inc (rand-int max-number-bytes)))))


(defn create-initial-population-1
  [population-size, chromosome-size]
  (let [max-number-of-bytes (get-max-number-of-bytes chromosome-size)]
    (vec (repeatedly population-size (fn [] (generate-chromosome-1 chromosome-size max-number-of-bytes))))))


;;The next step is to calculate the adaptation measure.
;; It is obtained by calculating the fitness function for each individual,
;; and then observing whether the individual can be in the population
;;
;;We will observe the correctness of the individual in the number of coding combinations.
;; For example, only two characters can be replaced with one bit value, because a character can
;; take the value 0 or 1. If more than 2 genes containing the value 1 appear in an individual,
;; we will declare it incorrect. Based on this logic,
;; we define the condition of non-acceptance of an individual:
;;
;; count(c)<=2^c
;;
;; where c is number of bytes to represent character
;;
;; In order to optimize the solution, we will sort each gene of each individual in ascending order

(defn calculate-individual-fitness
  "Calculating fitness function based on count of bytes for one individual"
  [individual letters]
  (let [freq (frequencies individual)]
    (if (every? #(<= (val %) (Math/pow 2 (key %))) freq)
      (reduce + (map * individual (vec (map second letters))))
      0)))

(defn survival
  [population letters]
  (filter #(< 0 (calculate-individual-fitness % letters)) population))

;;Now parents should be chosen based on their adaptability.
;; Individuals with better traits are more likely to live longer,
;; and therefore reproduce. In my case, the individual has better attributes
;; if it is represented with less memory
;;
;; The technique chosen by parents is the roulette technique.
;; Individuals with better fitness function will have a higher probability of being Parents,
;; but individuals with lower fitness function also have a chance to be parents.






