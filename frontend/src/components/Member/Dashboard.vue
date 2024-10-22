<template>
  <div>
    <Navbar />

    <!-- Image Carousel -->
    <div
      id="trainingCarousel"
      class="carousel slide mt-4"
      data-bs-ride="carousel"
    >
      <div class="carousel-indicators">
        <button
          type="button"
          data-bs-target="#trainingCarousel"
          data-bs-slide-to="0"
          class="active"
          aria-current="true"
          aria-label="Slide 1"
        ></button>
        <button
          type="button"
          data-bs-target="#trainingCarousel"
          data-bs-slide-to="1"
          aria-label="Slide 2"
        ></button>
        <button
          type="button"
          data-bs-target="#trainingCarousel"
          data-bs-slide-to="2"
          aria-label="Slide 3"
        ></button>
      </div>
      <div class="carousel-inner">
        <div class="carousel-item active">
          <img
            src="../../assets/logo.jpg"
            class="d-block w-100"
            alt="Fitness Class 1"
          />
          <div class="carousel-caption d-none d-md-block">
            <h5>Join Our Yoga Classes</h5>
            <p>Start your day with a refreshing yoga session.</p>
          </div>
        </div>
        <div class="carousel-item">
          <img
            src="../../assets/logo.jpg"
            class="d-block w-100"
            alt="Fitness Class 2"
          />
          <div class="carousel-caption d-none d-md-block">
            <h5>Advanced Pilates</h5>
            <p>Challenge yourself with our advanced pilates classes.</p>
          </div>
        </div>
        <div class="carousel-item">
          <img
            src="../../assets/logo.jpg"
            class="d-block w-100"
            alt="Fitness Class 3"
          />
          <div class="carousel-caption d-none d-md-block">
            <h5>CrossFit Workouts</h5>
            <p>Push your limits with intense CrossFit sessions.</p>
          </div>
        </div>
      </div>
      <button
        class="carousel-control-prev"
        type="button"
        data-bs-target="#trainingCarousel"
        data-bs-slide="prev"
      >
        <span class="carousel-control-prev-icon" aria-hidden="true"></span>
        <span class="visually-hidden">Previous</span>
      </button>
      <button
        class="carousel-control-next"
        type="button"
        data-bs-target="#trainingCarousel"
        data-bs-slide="next"
      >
        <span class="carousel-control-next-icon" aria-hidden="true"></span>
        <span class="visually-hidden">Next</span>
      </button>
    </div>

    <div class="container mt-5">
      <h1 class="mb-5 text-center">Welcome to MJ Training Camp!</h1>

      <!-- Overview of MJ Training Camp -->
      <div class="card mb-5">
        <div class="card-body">
          <h2 class="card-title">About MJ Training Camp</h2>
          <p class="card-text">
            MJ Training Camp is your premier fitness destination dedicated to
            helping you achieve your fitness goals. We offer a wide range of
            classes from beginner to advanced levels, ensuring that there's
            something for everyone.
          </p>
        </div>
      </div>

      <!-- Upcoming Classes and Membership Details -->
      <div class="row">
        <div class="col-lg-6 mb-4">
          <div class="card h-100 shadow-sm">
            <div class="card-body">
              <h3 class="card-title">Upcoming Classes</h3>
              <ul class="list-group list-group-flush">
                <li class="list-group-item d-flex align-items-center">
                  <i
                    class="fas fa-yoga me-3 text-success"
                    aria-hidden="true"
                  ></i>
                  <span>Yoga Basics - Monday, 10:00 AM</span>
                </li>
                <li class="list-group-item d-flex align-items-center">
                  <i
                    class="fas fa-dumbbell me-3 text-danger"
                    aria-hidden="true"
                  ></i>
                  <span>Advanced Pilates - Tuesday, 12:00 PM</span>
                </li>
                <li class="list-group-item d-flex align-items-center">
                  <i
                    class="fas fa-running me-3 text-primary"
                    aria-hidden="true"
                  ></i>
                  <span>CrossFit - Wednesday, 7:00 AM</span>
                </li>
              </ul>
            </div>
          </div>
        </div>

        <div class="col-lg-6 mb-4">
          <div class="card h-100 shadow-sm">
            <div class="card-body">
              <h3 class="card-title">Your Membership Details</h3>
              <p><strong>Membership Type:</strong> Gold</p>
              <p><strong>Status:</strong> Active until Jan 31, 2024</p>
              <p><strong>Classes Attended This Month:</strong> 8</p>
              <button class="btn btn-outline-primary mt-3">
                View Membership
              </button>
            </div>
          </div>
        </div>
      </div>

      <!-- Motivational Quote -->
      <div class="card mb-5">
        <div class="card-body">
          <h3 class="card-title">Stay Motivated!</h3>
          <blockquote class="blockquote mb-0">
            <p>
              "Every day is another chance to get stronger, to eat better, to
              live healthier, and to be the best version of you."
            </p>
            <footer class="blockquote-footer">Anonymous</footer>
          </blockquote>
        </div>
      </div>

      <!-- Additional Section: Testimonials -->
      <div class="card">
        <div class="card-body">
          <h3 class="card-title">What Our Members Say</h3>
          <div class="row">
            <div class="col-md-4">
              <div class="testimonial mb-4">
                <p>
                  "MJ Training Camp has transformed my fitness journey. The
                  instructors are fantastic!"
                </p>
                <footer class="blockquote-footer">- Sarah L.</footer>
              </div>
            </div>
            <div class="col-md-4">
              <div class="testimonial mb-4">
                <p>
                  "The variety of classes keeps me motivated and engaged every
                  week."
                </p>
                <footer class="blockquote-footer">- John D.</footer>
              </div>
            </div>
            <div class="col-md-4">
              <div class="testimonial mb-4">
                <p>
                  "A supportive community and top-notch facilities make MJ
                  Training Camp the best."
                </p>
                <footer class="blockquote-footer">- Emma K.</footer>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import Navbar from './Navbar.vue'

export default {
  name: 'MemberDashboard',
  components: {
    Navbar,
  },
}
</script>

<style scoped>
.container {
  background-color: #f4f7f6; /* Light background for contrast */
  border-radius: 8px;
  padding: 20px;
}

.card {
  border: none;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  transition: transform 0.2s;
}

.card:hover {
  transform: translateY(-5px); /* Lift effect on hover */
}

.card-title {
  color: #557c56;
  font-weight: bold;
}

.list-group-item {
  background-color: #ffffff;
  border-left: 3px solid transparent;
  padding: 12px;
  display: flex;
  align-items: center;
  transition: border-left-color 0.3s;
}

.list-group-item:hover {
  border-left-color: #557c56;
  background-color: #f0f9f0;
}

.list-group-item i {
  margin-right: 15px; /* Space between icon and text */
  font-size: 1.2em;
}

.blockquote {
  border-left: 5px solid #557c56;
  padding: 15px 20px;
  background-color: #e9f5eb;
  border-radius: 5px;
}

.blockquote footer {
  font-size: 0.9em;
  color: #555;
}

.carousel-inner img {
  height: 400px;
  object-fit: cover;
}

.carousel-caption {
  background: rgba(0, 0, 0, 0.5);
  padding: 10px;
  border-radius: 5px;
}

.testimonial {
  background-color: #f9f9f9;
  padding: 15px;
  border-left: 5px solid #557c56;
  border-radius: 5px;
  transition: background-color 0.3s;
}

.testimonial:hover {
  background-color: #eef6ee;
}
</style>
