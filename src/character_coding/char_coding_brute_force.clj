(ns character-coding.char-coding-brute-force
  (:require [clojure.math.combinatorics :as comb]))

; Add some input character and their frequences
(def letters [['A' 5]
              ['B' 1]
              ['C' 6]
              ['D' 3]])


; If we use the standard method of storing characters, each character is represented as an 8-bit value,
; and since there are a total of 15 letters, this text occupies 128 bits.
; We will try to find an algorithm so that each character represents a binary value, thus reducing the memory size needed for storage

(defn get-all-combinations [letters]
  "Computing all combinations. This is exhaustive and computationally expensive!"
  (let [only-letter (map first letters)]
    (mapv (fn [permutation]
            (mapv (fn [char value]
                    [char value]) only-letter permutation)) (comb/permutations (range (count only-letter))))))

(defn get-all-combinations-complete [letters]
  (let [letter (map first letters)
        freq (map second letters)]
    (mapv (fn [permutation]
            (mapv (fn [char freq value]
                    [char freq value]) letter freq permutation)) (comb/permutations (range (count letter))))))







