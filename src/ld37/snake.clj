(ns ld37.snake)

(declare spawn-food move-forward)

(defn setup-game
  [& {:keys [width height] :or {width 16 height 12}}]
  (let [[start-x start-y :as start-xy] [(int (/ width 2)) (int (/ height 2))]]
    (-> {:width width
         :height height
         :state :playing
         :direction :left
         :snake (list start-xy
                      [(inc start-x) start-y])
         :food nil}
        spawn-food)))

#_ (setup-game)
;; => {:width 32, :height 18, :state :playing, :direction :left, :snake ([16 9] [17 9])}


(defn empty-place?
  [{:keys [food snake]}
   [x y :as place]]
  (let [snake-positions (set snake)]
    (or (contains? food place)
        (contains? snake-positions place))))

(defn- spawn-food
  [{:keys [width height food]
    :as game}]
  (if food
    game ;; we already have food
    (loop []
      (let [food-pos [(rand-int width) (rand-int height)]]
        (if (empty-place? game food-pos)
          (recur)
          (assoc game :food food-pos))))))

#_ (-> (setup-game)
       (spawn-food)
       (spawn-food))

(defn move
  "dont make any turns, keep moving in same direction"
  [{:keys [state snake width height]
    [food-x food-y :as food] :food
    :as game}
   direction]
  (if (= state :playing)
    (let [[[head-x head-y :as head] & body] snake
          [prev-x prev-y :as prev-pos] (first body)
          [nx ny :as new-head] (case direction
                                 :left [(- head-x 1) head-y]
                                 :right [(+ head-x 1) head-y]
                                 :up [head-x (+ head-y 1)]
                                 :down [head-x (- head-y 1)])]
      (if (= prev-pos new-head)
        ;; going backwards not allowed, ignore and move forward
        (move-forward game)

        ;; move
        (if (or (< nx 0)
                (< ny 0)
                (>= nx width)
                (>= ny height)
                (contains? (set snake) new-head))
          ;; ran into something, die
          (do (println "DEAD!" direction game)
              (assoc game :state :dead))
          (-> (if (= food head)
                (-> game
                    (assoc :food nil)
                    (assoc :snake (concat [new-head]
                                          snake))
                    spawn-food)
                (-> game
                    (assoc :snake (concat [new-head]
                                          (butlast snake)))))
              (assoc :direction direction)))))
    game))


#_ (-> (setup-game)
       (move :left))
;; => {:width 16, :height 12, :state :playing, :direction :left, :snake ([7 6] [8 6]), :food nil}

(defn move-forward
  [{:keys [direction] :as game}]
  (move game direction))

#_ (-> (setup-game)
       move-forward
       move-forward)
;; => {:width 16, :height 12, :state :playing, :direction :left, :snake ([6 6] [7 6]), :food nil}
