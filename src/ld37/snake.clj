(ns ld37.snake)

(defn setup-game
  [& {:keys [width height] :or {width 16 height 12}}]
  (let [[start-x start-y :as start-xy] [(int (/ width 2)) (int (/ height 2))]]
    {:width width
     :height height
     :state :playing
     :direction :left
     :snake (list start-xy
                  [(inc start-x) start-y])
     :food #{}}))

#_ (setup-game)
;; => {:width 32, :height 18, :state :playing, :direction :left, :snake ([16 9] [17 9])}

(defn- spawn-food
  [{:keys [width height food snake]
    amount :num-of-food-to-spawn
    :as game}]
  (-> game
      ((fn [game]
         (let [snake-positions (set snake)]
           (loop [game game
                  amount amount]
             (if (zero? amount)
               game
               (let [food-pos [(rand-int width) (rand-int height)]]
                 (if (or (contains? food food-pos)
                         (contains? snake-positions food-pos))
                   (recur game amount)
                   (recur (-> game
                              (update :food conj food-pos))
                          (dec amount)))))))))))

#_ (-> (setup-game)
       (spawn-food)
       (spawn-food))

(defn move
  "dont make any turns, keep moving in same direction"
  [{:keys [state snake width height] :as game} direction]
  (if (= state :playing)
    (let [[[head-x head-y] & _] snake
          [nx ny :as new-head] (case direction
                                 :left [(- head-x 1) head-y]
                                 :right [(+ head-x 1) head-y]
                                 :up [head-x (+ head-y 1)]
                                 :down [head-x (- head-y 1)])]
      (if (or (< nx 0)
              (< ny 0)
              (>= nx width)
              (>= ny height)
              (contains? (set snake) new-head))
        ;; ran into something, die
        (assoc game :state :dead)
        ;; keep moving
        (-> game
            (assoc :direction direction)
            (assoc :snake (concat [new-head]
                                  (butlast snake))))))
    game))


#_ (-> (setup-game)
       (move :left))
;; => {:width 32, :height 18, :state :playing, :direction :left, :snake ([15 9] [16 9]), :food #{}}

(defn move-forward
  [{:keys [direction] :as game}]
  (move game direction))

#_ (-> (setup-game)
       move-forward
       move-forward)
;; => {:width 32, :height 18, :state :playing, :direction :left, :snake ([14 9] [15 9]), :food #{}}
