(ns ld37.snake)

(defn setup-game
  [& {:keys [width height] :or {width 16 height 9}}]
  (let [[start-x start-y :as start-xy] [(int (/ width 2)) (int (/ height 2))]]
    {:width width
     :height height
     :state :playing
     :direction :left
     :snake (list start-xy
                  [(inc start-x) start-y])}))

#_ (setup-game)
;; => {:width 16, :height 9, :state :playing, :direction :left, :snake ([8 4] [9 4])}

(defn- move
  "dont make any turns, keep moving in same direction"
  [{:keys [snake width height] :as game} direction]
  (let [[[head-x head-y] & _] snake
        [nx ny :as new-head] (case direction
                               :left [(- head-x 1) head-y]
                               :right [(+ head-x 1) head-y]
                               :up [head-x (- head-y 1)]
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
          (assoc :snake (concat new-head
                                (butlast snake)))))))


#_ (-> (setup-game)
       (move :left))
;; => {:width 16, :height 9, :state :playing, :direction :left, :snake (7 4 [8 4])}

(defn move-forward
  [{:keys [direction] :as game}]
  (move game direction))

#_ (-> (setup-game)
       move-forward)
