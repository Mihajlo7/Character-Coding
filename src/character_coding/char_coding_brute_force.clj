(ns character-coding.char-coding-brute-force
  (:require [clojure.math.combinatorics :as comb]
            [criterium.core :refer [quick-bench]]))

; Add some input character and their frequences
(def letters [['A' 5]
              ['B' 1]
              ['C' 6]
              ['D' 3]])

;-------------------------------------------
;; Optimized function

;;--------- NEW IMPLEMENTATION ------------

; If we use the standard method of storing characters, each character is represented as an 8-bit value,
; and since there are a total of 15 letters, this text occupies 128 bits.
; We will try to find an algorithm so that each character represents a binary value, thus reducing the memory size needed for storage

(defn generate-all-comb
  "Computing all combinations. This is exhaustive and computationally expensive!
  Generates matrix, which one row have count of letters "
  [letters]
  (vec (comb/permutations (range (count letters)))))

;;
;; For this algorithm, we will define a fitness function,
;; which is to find the smallest size of memory occupied by the given letters.
;; We will observe the frequency of each letter and construct the algorithm in such a way that
;; we need to assign a smaller binary value to characters that appear more frequently.

(defn calculate-individual-fitness
  "Calculating memory for one combinations in base frequencess"
  [individual letters]
  (loop [sum 0
         i 0]
    (if (< i (count individual))
      (recur (+ sum (* (count (Integer/toBinaryString (nth individual i)))
                       (second (nth letters i)))) (inc i))
      sum)))

(defn calculate-optimal-individual
  "Returns combination with best fitness fuction
  Input: all possible combinations and letters with frequences
  Output: combinations with best positions of decimal number"
  [combinations letters]
  (loop [iteration 0
         best-individual []
         best-memory-size Integer/MAX_VALUE]
    (if (< iteration (count combinations))
      (let [individual (nth combinations iteration)
            current-memory (calculate-individual-fitness individual letters)]
        (if (<= current-memory best-memory-size)
          (recur (inc iteration) individual current-memory )
          (recur (inc iteration) best-individual best-memory-size))
        )best-individual)))

(defn brute-force-algorithm
  [letters]
  (let [combinations (generate-all-comb letters)]
    (let [best-individual (calculate-optimal-individual combinations letters)]
      [(calculate-individual-fitness best-individual letters) best-individual])))



