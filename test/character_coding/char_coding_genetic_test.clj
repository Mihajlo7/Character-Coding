(ns character-coding.char-coding-genetic-test
  (:require [midje.sweet :refer :all]
            [character-coding.char-coding-genetic-alg :refer :all]))

(def letters-test [['A' 5]
                   ['B' 1]
                   ['C' 6]
                   ['D' 3]])
(def letters-test-count (count letters-test))

(def individual [1 2 3 2])
(fact "Testing count numbers of bytes to represent character. Input is length of characters"
      (get-max-number-of-bytes 5)=> 3
      (get-max-number-of-bytes 1)=> 1
      (get-max-number-of-bytes 0)=> 0
      (get-max-number-of-bytes (count letters-test))=> 2)

(fact "Testing function for generating random chromosome"
      (count (generate-chromosome 4 2))=> 4
      (generate-chromosome 0 0)=> []
      (generate-chromosome 0 1)=> []
      (generate-chromosome 0 2)=> []
      (generate-chromosome 1 0)=> [1]
      (count (generate-chromosome
               letters-test-count (get-max-number-of-bytes letters-test-count))) => 4)

(fact "Testing creating initial population"
      (count (create-initial-population 10 2))=> 10
      (create-initial-population 0 0)=> []
      (create-initial-population 0 1)=> []
      (create-initial-population 1 0)=> [[]]
      (create-initial-population 2 0)=> [[] []]
      (create-initial-population 1 1)=> [[1]]
      (create-initial-population 2 1)=> [[1] [1]])

(facts "Testing generate mutation points"
      (let [chromosome-length 10
            points (generate-mutation-point chromosome-length)]
            (fact "length of points"
                  (count points)=> 2)
            (fact "All numbers in 1 to chromosome and distinct"
                  (every? #(and (<= 1 % chromosome-length))points) => true
                  (distinct points)=> points)
            (fact "Sorting"
                  (apply < points)=> true)))

(facts "Testing inversion mutation"
       (let [individual [1 2 3 2]]
             (fact "Test case 1"
                   (inversion-mutation individual [2 3])=> '(1 3 2 2))
             (fact "Test case 2"
                   (inversion-mutation individual [1 4])=> '(2 3 2 1))
             (fact "Test case 3"
                   (inversion-mutation individual [1 2])=> '(2 1 3 2))
             (fact "Testing with 0"
                   (inversion-mutation individual [0 1])=> (throws java.lang.IndexOutOfBoundsException))))
