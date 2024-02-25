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
      (let [letters [['A' 5]
                     ['B' 1]
                     ['C' 6]
                     ['D' 3]]]
            (get-max-number-of-bytes 5)=> 3
            (get-max-number-of-bytes 1)=> 1
            (get-max-number-of-bytes 0)=> 0
            (get-max-number-of-bytes (count letters))=> 2))


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

(facts "Testing calculating memory for individual"
       (let [letters [['A' 5]
                      ['B' 1]
                      ['C' 6]
                      ['D' 3]]]
             (fact "Test case 1"
                   (calculate-memory [1 1 2 2] letters)=> 24)
             (fact "Test case 2"
                   (calculate-memory [1 2 1 2] letters)=> 19)
             (fact "Test bad case 1"
                   (calculate-memory [1 1 1 2] letters)=> 0
                   (calculate-memory [1 1 1 1] letters)=> 0))
       (let [letters [['A' 5]
                      ['B' 1]
                      ['C' 6]
                      ['D' 3]
                      ['E' 2]
                      ['F' 1]
                      ['G' 4]
                      ['H' 4]
                      ['I' 2]
                      ['J' 2]]]
             (fact "Bad coding 1"
                   (calculate-memory [3 3 1 1 2 2 1 3 3 3] letters)=> 0)
             (fact "Bad coding 2"
                   (calculate-memory [2 2 2 2 2 2 2 2 1 1] letters)=> 0)
             (fact "Bad coding 3"
                   (calculate-memory [3 3 3 3 3 3 3 3 3 3] letters)=> 0)
             (fact "Good coding"
                   (calculate-memory [4 4 4 4 4 4 4 4 4 4] letters)=> 120)))

(facts "Testing generating crossover points"
       (let [points (generate-xover-point 10)]
             (fact " First point"
                   (< 0 (first points))=> true)
             (fact "Second points"
                   (> 10 (second points) )=> true)))
(facts "Testing two points crossover"
       (fact "Number of children"
             (let [parent-1 [1 2 3 4 2]
                   parent-2 [2 3 1 1 2]
                   points  [2 3]
                   [child-1 child-2] (two-point-crossover parent-1 parent-2 points)]
                   (and (= (count child-1) (count parent-1))
                        (= (count child-2) (count parent-2)))))

       (fact "Checking children 1"
             (let [parent-1 [1 2 3 4 2]
                   parent-2 [2 3 1 1 2]
                   points  [2 3]
                   [child-1 child-2] (two-point-crossover parent-1 parent-2 points)]
                   (and (= child-1 [1 2 1 1 2])
                        (= child-2 [2 2 3 4 2]))))
       (fact "Checking children 2"
             (let [parent-1 [1 2 3 4 2]
                   parent-2 [2 3 1 1 2]
                   points  [1 4]
                   [child-1 child-2] (two-point-crossover parent-1 parent-2 points)]
                   (and (= child-1 [1 3 1 1 2])
                        (= child-2 [2 2 3 4 2])))))

(facts "Testing generating mutation points"
       (let [points (generate-mutation-point 10)]
             (fact "First point"
                   (< 0 (first points))=> true)
             (fact "Second point"
                   (<= (second points) 10)=> true)))

