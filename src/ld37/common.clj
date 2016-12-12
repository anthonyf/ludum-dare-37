(ns ld37.common)

(def title "Longcat")
(def subtitle "The Enlongening")

(def screen-size [1024 768])

(def tile-width 64)

(def game-speed 0.25)

(defn game-to-screen-pos-centered
  [actor [x y]]
  [(+ (- (* tile-width x)
         (/ (.getWidth actor) 2))
      (/ tile-width 2))
   (+ (- (* tile-width y)
         (/ (.getHeight actor) 2))
      (/ tile-width 2))])

(defn set-actor-game-position-centered
  [actor game-pos]
  (let [[x y] (game-to-screen-pos-centered actor game-pos)]
    (.setPosition actor x y)))


(defn set-actor-game-position
  [actor [x y :as game-pos]]
  (let [[x y] [(* tile-width x)
               (* tile-width y)]]
    (.setPosition actor x y)))
